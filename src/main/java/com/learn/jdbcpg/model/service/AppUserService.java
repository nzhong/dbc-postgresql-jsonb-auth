package com.learn.jdbcpg.model.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AppUserService {
	private static Logger log = LoggerFactory.getLogger(AppUserService.class);

	public static Map<String,String> getUserCredentialByUserName(final Connection conn, final String userName) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT userId, encPswd, encSalt FROM appUser WHERE userName = ? ";
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			stmt.setString(1, userName);
			rs = stmt.executeQuery();

			if ( rs.next() ) {
				int userId = rs.getInt(1);
				String encPswd = rs.getString(2);
				String encSalt = rs.getString(3);
				log.info( "For {}, found user id = {}", userName, userId );
				return new HashMap<String,String>() {{
					put( "userId", ""+userId );
					put( "encPswd", encPswd );
					put( "encSalt", encSalt );
				}};
			}
			else {
				return null;
			}
		}
		catch(SQLException sqlE) {
			log.warn("Exception when finding the ueseId of {}", userName, sqlE);
			return null;
		}
	}
}
