package com.github.sunnysuperman.commons.locale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sunnysuperman.commons.config.ConfigKeyFilter;
import com.github.sunnysuperman.commons.model.TimeSerializeType;
import com.github.sunnysuperman.commons.repository.RepositoryException;
import com.github.sunnysuperman.commons.repository.db.JdbcTemplate;
import com.github.sunnysuperman.commons.repository.db.MapHandler;
import com.github.sunnysuperman.commons.utils.ByteUtil;
import com.github.sunnysuperman.commons.utils.FormatUtil;
import com.github.sunnysuperman.commons.utils.JSONUtil;
import com.github.sunnysuperman.commons.utils.StringUtil;

public class DBLocaleBundle extends LocaleBundle {

	public static class DBLocaleBundleOptions extends LocaleBundleOptions {
		private JdbcTemplate jdbcTemplate;
		private String tableName;
		private String keyColumn;
		private String valueColumn;
		private String updatedAtColumn;
		private TimeSerializeType updatedAtSerializeType;
		private int reloadSeconds;
		private ConfigKeyFilter keyFilter;

		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public TimeSerializeType getUpdatedAtSerializeType() {
			return updatedAtSerializeType;
		}

		public void setUpdatedAtSerializeType(TimeSerializeType updatedAtSerializeType) {
			this.updatedAtSerializeType = updatedAtSerializeType;
		}

		public int getReloadSeconds() {
			return reloadSeconds;
		}

		public void setReloadSeconds(int reloadSeconds) {
			this.reloadSeconds = reloadSeconds;
		}

		public String getKeyColumn() {
			return keyColumn;
		}

		public void setKeyColumn(String keyColumn) {
			this.keyColumn = keyColumn;
		}

		public String getValueColumn() {
			return valueColumn;
		}

		public void setValueColumn(String valueColumn) {
			this.valueColumn = valueColumn;
		}

		public String getUpdatedAtColumn() {
			return updatedAtColumn;
		}

		public void setUpdatedAtColumn(String updatedAtColumn) {
			this.updatedAtColumn = updatedAtColumn;
		}

		public ConfigKeyFilter getKeyFilter() {
			return keyFilter;
		}

		public void setKeyFilter(ConfigKeyFilter keyFilter) {
			this.keyFilter = keyFilter;
		}

	}

	private static final Logger LOG = LoggerFactory.getLogger(DBLocaleBundle.class);
	private volatile Timer taskTimer = null;
	private volatile Date lastUpdateTime = null;

	public DBLocaleBundle(final DBLocaleBundleOptions options) {
		super(options);
		if (options.getUpdatedAtSerializeType() == null) {
			throw new IllegalArgumentException("updatedAtSerializeType");
		}
		if (StringUtil.isEmpty(options.getKeyColumn())) {
			options.setKeyColumn("id");
		}
		if (StringUtil.isEmpty(options.getValueColumn())) {
			options.setValueColumn("v");
		}
		if (options.getUpdatedAtColumn() == null) {
			options.setUpdatedAtColumn("updated_at");
		}
		loadAll(true);
		scheduleReloadWorker();
	}

	public DBLocaleBundleOptions getOptions() {
		return (DBLocaleBundleOptions) options;
	}

	public void close() {
		if (taskTimer != null) {
			taskTimer.cancel();
		}
	}

	private void scheduleReloadWorker() {
		int reloadSeconds = getOptions().getReloadSeconds();
		if (reloadSeconds <= 0) {
			return;
		}
		int period = reloadSeconds * 1000;
		if (taskTimer == null) {
			taskTimer = new Timer();
		}
		taskTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					loadAll(false);
				} catch (Throwable t) {
					LOG.error("Failed to update locale bundle", t);
				}
				try {
					scheduleReloadWorker();
				} catch (Throwable t) {
					LOG.error("Failed to scheduleReloadWorker", t);
				}
			}
		}, new Date(System.currentTimeMillis() + period));
	}

	private Object serializeUpdatedAt(Date date) {
		TimeSerializeType type = getOptions().getUpdatedAtSerializeType();
		if (type == TimeSerializeType.Date) {
			return date;
		} else if (type.equals(TimeSerializeType.Long)) {
			return date.getTime();
		} else {
			throw new RuntimeException("Bad updatedAt type: " + type);
		}
	}

	private Date deserializeUpdatedAt(Object v) {
		TimeSerializeType type = getOptions().getUpdatedAtSerializeType();
		if (type == TimeSerializeType.Date) {
			return (Date) v;
		} else if (type.equals(TimeSerializeType.Long)) {
			return new Date(FormatUtil.parseLong(v));
		} else {
			throw new RuntimeException("Bad updatedAt type: " + type);
		}
	}

	private void loadAll(boolean first) {
		String keyColumn = getOptions().getKeyColumn();
		String valueColumn = getOptions().getValueColumn();
		String updatedAtColumn = getOptions().getUpdatedAtColumn();
		List<Map<String, Object>> list;
		try {
			List<Object> params = new ArrayList<Object>(1);
			StringBuilder sb = new StringBuilder("select ").append(keyColumn).append(',').append(valueColumn)
					.append(",").append(updatedAtColumn).append(" from ").append(getOptions().getTableName());
			if (lastUpdateTime != null) {
				sb.append(" where " + updatedAtColumn + ">?");
				params.add(serializeUpdatedAt(lastUpdateTime));
			}
			sb.append(" order by ").append(updatedAtColumn).append(" asc");
			Object[] paramsArray = new Object[params.size()];
			params.toArray(paramsArray);
			list = getOptions().getJdbcTemplate().findList(sb.toString(), paramsArray, 0, 0, MapHandler.LOWERCASE);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
		if (!list.isEmpty()) {
			lastUpdateTime = deserializeUpdatedAt(list.get(list.size() - 1).get(updatedAtColumn));
			LOG.warn("Update locale table size: " + list.size());
			for (Map<String, Object> item : list) {
				String key = item.get(keyColumn).toString();
				ConfigKeyFilter keyFilter = getOptions().getKeyFilter();
				if (keyFilter != null && !keyFilter.accept(key)) {
					continue;
				}
				Object vRaw = item.get(valueColumn);
				String vAsString = null;
				if (vRaw instanceof String) {
					vAsString = (String) vRaw;
				} else {
					vAsString = ByteUtil.bytes2string((byte[]) vRaw);
				}
				Map<String, Object> map = JSONUtil.parseJSONObject(vAsString);
				if (map == null) {
					LOG.warn("Localized string is null for :" + key);
					continue;
				}
				if (!first && LOG.isInfoEnabled()) {
					LOG.info("Update locale string for '" + key + "': " + item.get(valueColumn));
				}
				for (Entry<String, Object> entry : map.entrySet()) {
					String locale = entry.getKey();
					String value = FormatUtil.parseString(entry.getValue());
					if (value == null) {
						continue;
					}
					if (first) {
						try {
							put(key, locale, value);
						} catch (Exception e) {
							if (e instanceof RuntimeException) {
								throw (RuntimeException) e;
							} else {
								throw new RuntimeException(e);
							}
						}
					} else {
						try {
							put(key, locale, value);
						} catch (Exception e) {
							LOG.error(null, e);
						}
					}
				}
			}
		}
		finishPut();
	}
}
