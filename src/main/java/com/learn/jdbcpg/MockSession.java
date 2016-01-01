package com.learn.jdbcpg;

import com.learn.jdbcpg.authentication.AuthUtil;
import com.learn.jdbcpg.model.service.AppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

public class MockSession {

    private static Logger log = LoggerFactory.getLogger(MockSession.class);

    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPswd;

    public MockSession(final String jdbcUrl, final String jdbcUser, final String jdbcPswd) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUser = jdbcUser;
        this.jdbcPswd = jdbcPswd;
        log.debug("jdbcUrl={}, jdbcUser={}, jdbcPswd={}", jdbcUrl, jdbcUser, jdbcPswd);
    }

    public void mockLogin(final String userName, final String plainTextPswd) throws IOException {
        int loginUserId = -1;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPswd);

            loginUserId = AuthUtil.validate(conn, userName, plainTextPswd);
            if (loginUserId > 0) {
                log.info("Mock session login success.");
                AppUserService.recordLogin(conn, loginUserId);
            } else {
                log.info("Mock session login failed.");
            }
        } catch (SQLException sqlE) {
            log.error("Exception: ", sqlE);
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
}
