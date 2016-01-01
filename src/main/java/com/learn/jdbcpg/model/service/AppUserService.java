package com.learn.jdbcpg.model.service;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.postgresql.util.PGobject;

public class AppUserService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Logger log = LoggerFactory.getLogger(AppUserService.class);

    public static Map<String, String> getUserCredentialByUserName(final Connection conn, final String userName) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT userId, encPswd, encSalt FROM appUser WHERE userName = ? ";
            stmt = (PreparedStatement) conn.prepareStatement(sql);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt(1);
                String encPswd = rs.getString(2);
                String encSalt = rs.getString(3);
                log.info("For {}, found user id = {}", userName, userId);
                return new HashMap<String, String>() {
                    {
                        put("userId", "" + userId);
                        put("encPswd", encPswd);
                        put("encSalt", encSalt);
                    }
                };
            } else {
                return null;
            }
        } catch (SQLException sqlE) {
            log.warn("Exception when finding the ueseId of {}", userName, sqlE);
            return null;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException ex) {}
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException ex) {}
            }
        }
    }
    
    public static void recordLogin(final Connection conn, final int userId) throws SQLException, IOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT logins FROM appUser WHERE userId = ? ";
            stmt = (PreparedStatement) conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                List<Object> logins = null;
                PGobject jsonObject = (PGobject) rs.getObject(1);
                if ( jsonObject!=null && jsonObject.getValue()!=null ) {
                    logins = mapper.readValue(jsonObject.getValue(), List.class);
                }
                else {
                    jsonObject = new PGobject();
                    jsonObject.setType("text");
                    logins = new ArrayList<>();
                }
                logins.add( LocalDateTime.now() );
                jsonObject.setValue(mapper.writeValueAsString(logins));
                
                try { rs.close(); } catch (SQLException ex) {}
                try { stmt.close(); } catch (SQLException ex) {}
                
                stmt = conn.prepareStatement("UPDATE appUser SET logins = CAST(? AS jsonb) WHERE userId = ?");
		stmt.setObject(1, jsonObject);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            } else {
                throw new SQLException("userId not found for "+userId);
            }
        }
        finally {
            try { rs.close(); } catch (SQLException ex) {}
            try { stmt.close(); } catch (SQLException ex) {}
        }
    }
}
