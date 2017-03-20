package com.github.sunnysuperman.commons.repository.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBUtil {

	public static void closeQuietly(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			// quiet
		}
	}

	public static void closeQuietly(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			// quiet
		}
	}

	public static void closeQuietly(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			// quiet
		}
	}

	public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {
		try {
			closeQuietly(rs);
		} finally {
			try {
				closeQuietly(stmt);
			} finally {
				closeQuietly(conn);
			}
		}
	}

	public static void closeQuietly(Connection conn, Statement stmt) {

		try {
			closeQuietly(stmt);
		} finally {
			closeQuietly(conn);
		}

	}

	public static void closeQuietly(Statement stmt, ResultSet rs) {
		try {
			closeQuietly(rs);
		} finally {
			closeQuietly(stmt);
		}

	}
}
