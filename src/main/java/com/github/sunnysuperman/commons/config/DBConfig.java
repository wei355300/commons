package com.github.sunnysuperman.commons.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sunnysuperman.commons.repository.RepositoryException;
import com.github.sunnysuperman.commons.repository.db.JdbcTemplate;
import com.github.sunnysuperman.commons.repository.db.MapHandler;
import com.github.sunnysuperman.commons.utils.FormatUtil;
import com.github.sunnysuperman.commons.utils.JSONUtil;

/**
 * 数据库配置类
 * 
 * 
 *
 */
public class DBConfig extends ByteStoredConfig {
	public static final String DATETYPE_DATE = "date";
	public static final String DATETYPE_TIMESTAMP = "timestamp";
	private static final Logger LOGGER = LoggerFactory.getLogger(DBConfig.class);

	protected DBConfigOptions options;
	private JdbcTemplate template;
	private volatile Timer taskTimer = null;
	private volatile Date lastUpdateTime = null;

	/**
	 * 数据库配置选项类
	 * 
	 * 
	 *
	 */
	public static class DBConfigOptions {
		private JdbcTemplate jdbcTemplate;
		private String tableName;
		private String keyColumn;
		private String valueColumn;
		private String typeColumn;
		private String updatedAtColumn;
		private String updatedAtSerializeType;
		private boolean typeUndeclared;
		private String defaultType;
		private boolean loadOnInit;
		private ConfigKeyFilter keyFilter;
		private boolean reloadable;
		private String reloadSecondsKey;
		private int reloadSeconds;

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

		public String getTypeColumn() {
			return typeColumn;
		}

		public void setTypeColumn(String typeColumn) {
			this.typeColumn = typeColumn;
		}

		public boolean isLoadOnInit() {
			return loadOnInit;
		}

		public void setLoadOnInit(boolean loadOnInit) {
			this.loadOnInit = loadOnInit;
		}

		public ConfigKeyFilter getKeyFilter() {
			return keyFilter;
		}

		public void setKeyFilter(ConfigKeyFilter keyFilter) {
			this.keyFilter = keyFilter;
		}

		public boolean isReloadable() {
			return reloadable;
		}

		public void setReloadable(boolean reloadable) {
			this.reloadable = reloadable;
		}

		public String getReloadSecondsKey() {
			return reloadSecondsKey;
		}

		public void setReloadSecondsKey(String reloadSecondsKey) {
			this.reloadSecondsKey = reloadSecondsKey;
		}

		public String getUpdatedAtSerializeType() {
			return updatedAtSerializeType;
		}

		public void setUpdatedAtSerializeType(String updatedAtSerializeType) {
			this.updatedAtSerializeType = updatedAtSerializeType;
		}

		public boolean isTypeUndeclared() {
			return typeUndeclared;
		}

		public void setTypeUndeclared(boolean typeUndeclared) {
			this.typeUndeclared = typeUndeclared;
		}

		public String getDefaultType() {
			return defaultType;
		}

		public void setDefaultType(String defaultType) {
			this.defaultType = defaultType;
		}

		public int getReloadSeconds() {
			return reloadSeconds;
		}

		public void setReloadSeconds(int reloadSeconds) {
			this.reloadSeconds = reloadSeconds;
		}

		public String getUpdatedAtColumn() {
			return updatedAtColumn;
		}

		public void setUpdatedAtColumn(String updatedAtColumn) {
			this.updatedAtColumn = updatedAtColumn;
		}

	}

	/**
	 * 构造函数<br>
	 * 初次加载所有数据<br>
	 * 启动reaload线程，定时刷新配置表
	 * 
	 * @param options
	 *            数据库配置选项，默认配置：主键列id，值列value，类型列type，不过滤任何key
	 */
	public DBConfig(DBConfigOptions options) {
		if (options.getKeyColumn() == null) {
			options.setKeyColumn("id");
		}
		if (options.getValueColumn() == null) {
			options.setValueColumn("value");
		}
		if (options.isTypeUndeclared() && options.getDefaultType() == null) {
			throw new RuntimeException("No default type set");
		} else if (options.getTypeColumn() == null) {
			options.setTypeColumn("type");
		}
		if (options.getUpdatedAtColumn() == null) {
			options.setUpdatedAtColumn("updated_at");
		}
		if (options.getUpdatedAtSerializeType() == null) {
			options.setUpdatedAtSerializeType(DATETYPE_DATE);
		}
		if (options.getKeyFilter() == null) {
			options.setKeyFilter(new AllConfigKeyFilter());
		}
		this.options = options;
		template = options.getJdbcTemplate();
		if (options.isLoadOnInit()) {
			loadAll(true);
		}
		if (options.isReloadable()) {
			scheduleReloadWorker();
		}
	}

	/**
	 * 获取配置选项
	 * 
	 * @return 数据库配置选项
	 */
	public DBConfigOptions getOptions() {
		return options;
	}

	/**
	 * 比较是否是同一对象
	 * 
	 * @param oldValue
	 * @param newValue
	 * @param type
	 *            列类型
	 * @return 若任一对象为null，则返回false；否则返回true
	 */
	protected boolean isSameValue(Object oldValue, Object newValue, String type) {
		if (oldValue == newValue) { // Both null or both not null
			return true;
		}
		if (oldValue == null || newValue == null) {
			return false;
		}
		// Both not null
		if (type.equals(TYPE_JSONOBJECT)) {
			if (!(oldValue instanceof String)) {
				oldValue = JSONUtil.toJSONString(oldValue);
			}
			if (!(newValue instanceof String)) {
				newValue = JSONUtil.toJSONString(newValue);
			}
		} else if (type.equals(TYPE_BLOB)) {
			return false;
		}
		return oldValue.equals(newValue);
	}

	/**
	 * 刷新配置表的定时器，若未配置时间，默认为600秒刷新
	 */
	private void scheduleReloadWorker() {
		int reloadSeconds = options.getReloadSeconds();
		if (reloadSeconds <= 0) {
			reloadSeconds = options.getReloadSecondsKey() != null ? getInt(options.getReloadSecondsKey(), 0) : 0;
			if (reloadSeconds <= 0) {
				reloadSeconds = 600;
			}
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
					LOGGER.error("Failed to update db config", t);
				}
				try {
					scheduleReloadWorker();
				} catch (Throwable t) {
					LOGGER.error("Failed to scheduleReloadWorker", t);
				}
			}
		}, new Date(System.currentTimeMillis() + period));
	}

	private Object serializeUpdatedAt(Date date) {
		String type = options.getUpdatedAtSerializeType();
		if (type == null || type.equals(DATETYPE_DATE)) {
			return date;
		} else if (type.equals(DATETYPE_TIMESTAMP)) {
			return date.getTime();
		} else {
			throw new RuntimeException("Bad updatedAt type: " + type);
		}
	}

	private Date deserializeUpdatedAt(Object v) {
		String type = options.getUpdatedAtSerializeType();
		if (type == null || type.equals(DATETYPE_DATE)) {
			return (Date) v;
		} else if (type.equals(DATETYPE_TIMESTAMP)) {
			return new Date(FormatUtil.parseLong(v));
		} else {
			throw new RuntimeException("Bad updatedAt type: " + type);
		}
	}

	/**
	 * 加载所有数据
	 */
	private void loadAll(boolean first) {
		List<Map<String, Object>> list;
		try {
			List<Object> params = new ArrayList<Object>(1);
			StringBuilder sb = new StringBuilder("select ").append(options.getKeyColumn()).append(',')
					.append(options.getValueColumn()).append(",").append(options.getUpdatedAtColumn());
			if (!options.isTypeUndeclared()) {
				sb.append(',').append(options.getTypeColumn());
			}
			sb.append(" from ").append(options.getTableName());
			if (lastUpdateTime != null) {
				sb.append(" where " + options.getUpdatedAtColumn() + ">?");
				params.add(serializeUpdatedAt(lastUpdateTime));
			}
			sb.append(" order by ").append(options.getUpdatedAtColumn()).append(" asc");
			String sql = sb.toString();
			Object[] paramsArray = params.toArray(new Object[params.size()]);
			list = template.findList(sql, paramsArray, 0, 0, MapHandler.CASESENSITIVE);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
		if (!list.isEmpty()) {
			lastUpdateTime = deserializeUpdatedAt(list.get(list.size() - 1).get(options.getUpdatedAtColumn()));
			for (Map<String, Object> item : list) {
				String key = item.get(options.getKeyColumn()).toString();
				if (!options.getKeyFilter().accept(key)) {
					continue;
				}
				String type = FormatUtil.parseString(item.get(options.getTypeColumn()), options.getDefaultType());
				Object value = deserialize(type, (byte[]) item.get(options.getValueColumn()));
				TypeAndValue tv = new TypeAndValue(type, value);
				boolean notifyChanged = !first;
				if (notifyChanged) {
					Object oldValue = getValue(key);
					notifyChanged = !isSameValue(oldValue, value, type);
				}
				if (notifyChanged && LOGGER.isInfoEnabled()) {
					LOGGER.info("config value changed of key '" + key + "'");
				}
				put(key, tv, notifyChanged);
			}
		}
	}

	/**
	 * 关闭方法，停止刷新定时器
	 */
	public void destroy() {
		if (taskTimer != null) {
			taskTimer.cancel();
		}
	}

	@Override
	public void save(String key, String type, Object value) throws RepositoryException {
		if (!isExplicitType(type)) {
			throw new IllegalArgumentException("Unknown type: " + type);
		}
		StringBuilder sb = new StringBuilder("select count(*) from ").append(options.getTableName()).append(" where ")
				.append(options.getKeyColumn()).append("=?");
		boolean exists = template.count(sb.toString(), new Object[] { key }) > 0;
		Map<String, Object> doc = new HashMap<String, Object>();
		doc.put("value", serialize(type, value));
		doc.put(options.getUpdatedAtColumn(), serializeUpdatedAt(new Date()));
		if (exists) {
			template.update(options.getTableName(), doc, options.getKeyColumn(), key);
		} else {
			doc.put("type", type);
			doc.put(options.getKeyColumn(), key);
			template.insert(options.getTableName(), doc);
		}
	}

	@Override
	public void purge(String key) throws RepositoryException {
		template.update("delete from " + options.getTableName() + " where " + options.getKeyColumn() + "=?",
				new Object[] { key });
	}

	@Override
	protected TypeAndValue load(String key) throws RepositoryException {
		if (options.loadOnInit) {
			return null;
		}
		StringBuilder sb = new StringBuilder("select ").append(options.getValueColumn());
		if (!options.isTypeUndeclared()) {
			sb.append(',').append(options.getTypeColumn());
		}
		sb.append(" from ").append(options.getTableName()).append(" where ").append(options.getKeyColumn())
				.append("=?");
		Map<String, Object> item = template.find(sb.toString(), new Object[] { key }, MapHandler.CASESENSITIVE);
		if (item == null) {
			return null;
		}
		String type = FormatUtil.parseString(item.get(options.getTypeColumn()), options.getDefaultType());
		Object value = deserialize(type, (byte[]) item.get(options.getValueColumn()));
		TypeAndValue tv = new TypeAndValue(type, value);
		return tv;
	}

}
