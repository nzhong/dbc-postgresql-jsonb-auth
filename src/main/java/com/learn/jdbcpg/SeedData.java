package com.learn.jdbcpg;

import com.learn.crypto.util.MiscUtil;
import com.learn.jdbcpg.model.dao.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class SeedData {

    private static Logger log = LoggerFactory.getLogger(SeedData.class);

    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPswd;

    public SeedData(final String jdbcUrl, final String jdbcUser, final String jdbcPswd) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUser = jdbcUser;
        this.jdbcPswd = jdbcPswd;
        log.debug("jdbcUrl={}, jdbcUser={}, jdbcPswd={}", jdbcUrl, jdbcUser, jdbcPswd);
    }

    public void seed(AppUser seedUser) throws SQLException, IOException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPswd);
            boolean bUserTblExist = tableExist(conn, "appUser");
            log.info("appUser exist = {}", bUserTblExist);

            if (!bUserTblExist) {
                StringBuilder sqlCreateStmt = new StringBuilder()
                    .append("CREATE TABLE appUser (")
                    .append("  userId SERIAL PRIMARY KEY, ")
                    .append("  userName  TEXT    UNIQUE   NOT NULL, ")
                    .append("  encPswd   TEXT             NOT NULL, ")
                    .append("  encSalt   TEXT             NOT NULL, ")
                    .append("  firstName TEXT, ")
                    .append("  lastName  TEXT, ")
                    .append("  displayName  TEXT          NOT NULL, ")
                    .append("  emailAddr    TEXT, ")
                    .append("  phoneNumber  TEXT, ")
                    .append("  createdBy       INT        NOT NULL, ")
                    .append("  creationDate    TIMESTAMP WITH TIME ZONE NOT NULL, ")
                    .append("  lastUpdatedBy   INT        NOT NULL, ")
                    .append("  lastUpdateDate  TIMESTAMP WITH TIME ZONE NOT NULL, ")
                    .append("  lastLoginDate   TIMESTAMP WITH TIME ZONE, ")
                    .append("  roles  JSONB, ")
                    .append("  logins JSONB, ")
                    .append("  others JSONB ")
                    .append(")");
                stmt = conn.createStatement();
                stmt.executeUpdate(sqlCreateStmt.toString());
                stmt.close();
                log.info("appUser CREATED");
            }

            bUserTblExist = tableExist(conn, "appUser");
//			if ( bUserTblExist ) {
//				StringBuilder sqlInsertStmt = new StringBuilder()
//						.append("INSERT INTO appUser ")
//						.append("  (userName, encPswd, encSalt, displayName, createdBy, creationDate, lastUpdatedBy, lastUpdateDate) ")
//						.append("VALUES ")
//						.append("  ('guest', 'welcome', 'welcome', 'guest', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP ) ");
//				try {
//					stmt = conn.createStatement();
//					stmt.executeUpdate(sqlInsertStmt.toString());
//			        stmt.close();
//					log.info("appUser SEEDED");
//				} catch (SQLException ex) {
//				} // exception is OK since we may have already seeded in the last run
//			}

            if (bUserTblExist) {
                try {
                    seedUser.SerializeToDb(conn, 1);
                } catch (SQLException ex) {
                } // exception is OK since we may have already seeded in the last run

                // Let's see some more users
                AppUser u1 = new AppUser("u1", "User 1", MiscUtil.getRandomString());
                u1.setOthers( new HashMap<String,Object>() {{ 
                    put("Gender", "M");
                }} );
                try { u1.SerializeToDb(conn, 1); } catch (SQLException ex) {}
                
                AppUser u2 = new AppUser("u2", "User 2", MiscUtil.getRandomString());
                u2.setOthers( new HashMap<String,Object>() {{ 
                    put("Gender", "F");
                }} );
                try { u2.SerializeToDb(conn, 1); } catch (SQLException ex) {}
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    private boolean tableExist(final Connection conn, final String tblName) throws SQLException {
        final DatabaseMetaData meta = conn.getMetaData();

        ResultSet rs = null;
        try {
            rs = meta.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName != null && tableName.equalsIgnoreCase(tblName)) {
                    return true;
                }
            }
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
            }
        }
    }
}
