package com.learn.jdbcpg;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DbInfo {
	private static Logger log = LoggerFactory.getLogger(DbInfo.class);

	private final String jdbcUrl;
	private final String jdbcUser;
	private final String jdbcPswd;

	public DbInfo(final String jdbcUrl, final String jdbcUser, final String jdbcPswd) {
		this.jdbcUrl = jdbcUrl;
		this.jdbcUser = jdbcUser;
		this.jdbcPswd = jdbcPswd;
		log.debug( "jdbcUrl={}, jdbcUser={}, jdbcPswd={}", jdbcUrl, jdbcUser, jdbcPswd);
	}

	public String printDbInfo() {
		StringBuilder buf = new StringBuilder();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPswd);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT VERSION()");
			buf.append("SELECT VERSION(): ");
			while (rs.next()) {
				buf.append(rs.getString(1)).append("\n");
			}
			rs.close();

			DatabaseMetaData meta = conn.getMetaData();
			rs = meta.getCatalogs();
			buf.append("getCatalogs(): \n");
			while( rs.next() ) {
				buf.append("  ").append(rs.getString(1)).append("\n");
			}
			rs.close();

			rs = meta.getSchemas();
			buf.append("getSchemas(): \n");
			while( rs.next() ) {
				buf.append("  ").append(rs.getString(1)).append("\n");
			}
			rs.close();

			rs = meta.getTables(null, null, null, new String[] {"TABLE"});
			buf.append("getTables(): \n");
			while (rs.next()) {
				buf.append(
					"  (Cat)"+rs.getString("TABLE_CAT") +
					", (Schema)"+rs.getString("TABLE_SCHEM") +
					", (Table)"+rs.getString("TABLE_NAME") +
					", (Type)"+rs.getString("TABLE_TYPE") +
					", (Remarks)"+rs.getString("REMARKS"))
				.append("\n");
			}
		}
		catch (Exception e) {
			return ""+e;
		}
		finally {
			if (rs != null) {
				try { rs.close(); } catch (SQLException ex) {}
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException ex) {}
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException ex) {}
			}
		}
		return buf.toString();
	}
}
