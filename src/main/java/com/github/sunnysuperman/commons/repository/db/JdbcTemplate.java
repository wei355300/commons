package com.github.sunnysuperman.commons.repository.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sunnysuperman.commons.repository.RepositoryException;
import com.github.sunnysuperman.commons.utils.FormatUtil;
import com.github.sunnysuperman.commons.utils.Pagination;
import com.github.sunnysuperman.commons.utils.StringUtil;

public class JdbcTemplate {
	public static final String DB_MYSQL = "mysql";
	public static final String DB_ORACLE = "oracle";
	public static final String DB_POSTGRESQL = "postgresql";

	private volatile Logger LOGGER = LoggerFactory.getLogger(JdbcTemplate.class);
	private DataSource dataSource;
	private String dbType = DB_MYSQL;

	public JdbcTemplate() {
	}

	public JdbcTemplate(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public JdbcTemplate(DataSource dataSource, String dbType) {
		super();
		this.dataSource = dataSource;
		this.dbType = dbType;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public String getDbType() {
		return dbType;
	}

	public void setLogger(Logger logger) {
		if (logger == null) {
			throw new IllegalArgumentException("logger could not be null");
		}
		LOGGER = logger;
	}

	// //////////////////////////////////////////////////

	private void setParams(PreparedStatement ps, Object[] params) throws SQLException {
		if (params == null || params.length == 0) {
			return;
		}
		for (int i = 1; i <= params.length; i++) {
			ps.setObject(i, params[i - 1]);
		}
	}

	private void setParams(PreparedStatement ps, List<Object> params) throws SQLException {
		int size = params == null ? 0 : params.size();
		if (size == 0) {
			return;
		}
		int i = 0;
		for (Object param : params) {
			ps.setObject(i + 1, param);
			i++;
		}
	}

	public String getInsertSql(Map<String, Object> kv, String tableName, List<Object> params, boolean ignore) {
		StringBuilder buf = new StringBuilder(ignore ? "insert ignore into " : "insert into ");
		buf.append(tableName);
		buf.append('(');

		StringBuilder buf2 = new StringBuilder();

		int i = -1;
		for (Entry<String, Object> entry : kv.entrySet()) {
			i++;
			if (i > 0) {
				buf.append(',');
				buf2.append(',');
			}
			buf.append(StringUtil.camel2underline(entry.getKey()));
			Object fieldValue = entry.getValue();
			if (fieldValue != null && fieldValue instanceof CustomFunction) {
				CustomFunction cf = (CustomFunction) fieldValue;
				buf2.append(cf.getFunction());
				if (cf.getParams() != null) {
					for (Object param : cf.getParams()) {
						params.add(param);
					}
				}
			} else {
				buf2.append('?');
				params.add(fieldValue);
			}
		}

		buf.append(") values(");
		buf.append(buf2);
		buf.append(')');
		return buf.toString();
	}

	public String getPageDialect(String sql, int offset, int limit) {
		if (limit <= 0) {
			return sql;
		}
		if (dbType.equals(DB_MYSQL)) {
			return getMysqlDialect(sql, offset, limit);
		} else if (dbType.equals(DB_ORACLE)) {
			return getOracleDialect(sql, offset, limit);
		} else if (dbType.equals(DB_POSTGRESQL)) {
			return getPostgreDialect(sql, offset, limit);
		}
		throw new RuntimeException("Not support page dialect: " + dbType);
	}

	public static String getMysqlDialect(String sql, int offset, int limit) {
		return new StringBuilder(sql).append(" limit ").append(offset).append(",").append(limit).toString();
	}

	public static String getOracleDialect(String sql, int offset, int limit) {
		return new StringBuilder("select * from ( select row_.*, rownum rownum_ from ( ").append(sql)
				.append(" ) row_ where rownum <= ").append(offset + limit).append(" ) where rownum_ > " + offset)
				.toString();
	}

	public static String getPostgreDialect(String sql, int offset, int limit) {
		return new StringBuilder(sql).append(" limit ").append(limit).append(" offset ").append(offset).toString();
	}

	// //////////////////////////////////////////////////

	public int update(String sql, Object[] params) throws RepositoryException {
		long t1 = 0;
		if (LOGGER.isInfoEnabled()) {
			t1 = System.currentTimeMillis();
		}
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			setParams(ps, params);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new RepositoryException(e);
		} finally {
			DBUtil.closeQuietly(con, ps);
			if (LOGGER.isInfoEnabled()) {
				long take = System.currentTimeMillis() - t1;
				LOGGER.info("JDBCTemplate--->:" + sql + ", using: " + take + "ms");
			}
		}
	}

	public void batchUpdate(String sql, List<Object[]> paramArrayList) throws RepositoryException {
		long t1 = 0;
		if (LOGGER.isInfoEnabled()) {
			t1 = System.currentTimeMillis();
		}
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			for (Iterator<Object[]> it = paramArrayList.iterator(); it.hasNext();) {
				setParams(ps, it.next());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			throw new RepositoryException(e);
		} finally {
			DBUtil.closeQuietly(con, ps);
			if (LOGGER.isInfoEnabled()) {
				long take = System.currentTimeMillis() - t1;
				LOGGER.info("JDBCTemplate--->:" + sql + ", using: " + take + "ms");
			}
		}
	}

	public <T> T insert(String tableName, Map<String, Object> doc, RowHandler<T> handler, boolean ignore)
			throws RepositoryException {
		long t1 = 0;
		if (LOGGER.isInfoEnabled()) {
			t1 = System.currentTimeMillis();
		}
		List<Object> params = new ArrayList<Object>(doc.size());
		String sql = getInsertSql(doc, tableName, params, ignore);
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			if (handler == null) {
				ps = con.prepareStatement(sql);
				setParams(ps, params);
				ps.executeUpdate();
				return null;
			} else {
				ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				setParams(ps, params);
				ps.executeUpdate();
				rs = ps.getGeneratedKeys();
				return handler.handle(rs);
			}
		} catch (SQLException e) {
			throw new RepositoryException(e);
		} finally {
			DBUtil.closeQuietly(con, ps, rs);
			if (LOGGER.isInfoEnabled()) {
				long take = System.currentTimeMillis() - t1;
				LOGGER.info("JDBCTemplate--->:" + sql + ", using: " + take + "ms");
			}
		}
	}

	public <T> T insert(String tableName, Map<String, Object> doc, RowHandler<T> handler) throws RepositoryException {
		return insert(tableName, doc, handler, false);
	}

	public void insert(String tableName, Map<String, Object> doc) throws RepositoryException {
		insert(tableName, doc, null, false);
	}

	public int update(String tableName, Map<String, Object> doc, String primaryKey, Object primaryValue)
			throws RepositoryException {
		StringBuilder buf = new StringBuilder("update ");
		buf.append(tableName);
		buf.append(" set ");
		List<Object> params = new LinkedList<Object>();
		int offset = 0;
		for (Entry<String, Object> entry : doc.entrySet()) {
			String fieldKey = entry.getKey();
			Object fieldValue = entry.getValue();
			if (fieldKey.indexOf('$') == 0) {
				if (fieldKey.equals("$inc")) {
					Map<?, ?> fieldValueAsMap = (Map<?, ?>) fieldValue;
					for (Entry<?, ?> incEntry : fieldValueAsMap.entrySet()) {
						String incKey = StringUtil.camel2underline(incEntry.getKey().toString());
						Number incValue = FormatUtil.parseNumber(incEntry.getValue());
						if (offset > 0) {
							buf.append(",");
						}
						offset++;
						buf.append(incKey).append("=").append(incKey).append("+?");
						params.add(incValue);
					}
				}
				continue;
			}
			if (offset > 0) {
				buf.append(",");
			}
			offset++;
			buf.append(StringUtil.camel2underline(fieldKey));
			if (fieldValue != null && fieldValue instanceof CustomFunction) {
				// column=ST_GeometryFromText(?,4326), column=point(?,?), etc.
				CustomFunction cf = (CustomFunction) fieldValue;
				buf.append("=").append(cf.getFunction());
				if (cf.getParams() != null) {
					for (Object param : cf.getParams()) {
						params.add(param);
					}
				}
			} else {
				buf.append("=?");
				params.add(fieldValue);
			}
		}
		buf.append(" where ");
		if (primaryKey.indexOf(',') > 0) {
			List<String> primaryKeys = StringUtil.split(primaryKey, ",");
			// where pk1=? and pk2=?
			Object[] primaryValues = (Object[]) primaryValue;
			for (int i = 0; i < primaryKeys.size(); i++) {
				String pk = primaryKeys.get(i).trim();
				if (i > 0) {
					buf.append(" and ");
				}
				buf.append(StringUtil.camel2underline(pk));
				buf.append("=?");
				params.add(primaryValues[i]);
			}
		} else {
			buf.append(StringUtil.camel2underline(primaryKey));
			buf.append("=?");
			params.add(primaryValue);
		}
		Object[] paramsAsArray = params.toArray(new Object[params.size()]);
		return update(buf.toString(), paramsAsArray);
	}

	// /////////////////////////////////////////////

	public <T> T find(String sql, Object[] params, Handler<T> handler) throws RepositoryException {
		long t1 = 0;
		if (LOGGER.isInfoEnabled()) {
			t1 = System.currentTimeMillis();
		}
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			setParams(ps, params);
			rs = ps.executeQuery();
			return handler.handle(rs);
		} catch (SQLException ex) {
			throw new RepositoryException(ex);
		} finally {
			DBUtil.closeQuietly(con, ps, rs);
			if (LOGGER.isInfoEnabled()) {
				long take = System.currentTimeMillis() - t1;
				LOGGER.info("JDBCTemplate--->:" + sql + ", using: " + take + "ms");
			}
		}
	}

	public int count(String sql, Object[] params) throws RepositoryException {
		Object obj = find(sql, params, PropertyHandler.getInstance());
		if (obj instanceof Integer) {
			return ((Integer) obj).intValue();
		} else {
			return ((Long) obj).intValue();
		}
	}

	public <T> T findOne(String sql, Object[] params, RowHandler<T> handler) throws RepositoryException {
		return find(getPageDialect(sql, 0, 1), params, handler);
	}

	public <T> Pagination<T> findPaged(String sql, String count_sql, Object[] params, final int offset,
			final int limit, RowHandler<T> handler) throws RepositoryException {
		List<T> items = find(getPageDialect(sql, offset, limit), params, new ListHandler<T>(handler, limit));
		int records = items.size();
		if (records == 0) {
			return new Pagination<T>();
		}
		if (offset != 0 || records == limit) {
			records = count(count_sql, params);
		}
		return new Pagination<T>(items, records, offset, limit);
	}

	public <T> List<T> findList(String sql, Object[] params, final int offset, final int limit, RowHandler<T> handler)
			throws RepositoryException {
		return find(getPageDialect(sql, offset, limit), params, new ListHandler<T>(handler, limit));
	}

	public <T> Set<T> findSet(String sql, Object[] params, final int offset, final int limit, RowHandler<T> handler)
			throws RepositoryException {
		return find(getPageDialect(sql, offset, limit), params, new SetHandler<T>(handler, limit));
	}

	// TODO XXX
	public <T> Pagination<T> findPaged(String sql, String count_sql, Object[] params, final int offset,
			final int limit, ListHandler<T> handler) throws RepositoryException {
		List<T> items = find(getPageDialect(sql, offset, limit), params, handler);
		int records = items.size();
		if (records == 0) {
			return new Pagination<T>();
		}
		if (offset != 0 || records == limit) {
			records = count(count_sql, params);
		}
		return new Pagination<T>(items, records, offset, limit);
	}

	// TODO XXX
	public <T> List<T> findList(String sql, Object[] params, final int offset, final int limit, ListHandler<T> handler)
			throws RepositoryException {
		return find(getPageDialect(sql, offset, limit), params, handler);
	}

}
