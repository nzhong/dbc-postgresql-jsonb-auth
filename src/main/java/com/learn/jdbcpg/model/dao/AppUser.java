package com.learn.jdbcpg.model.dao;


import com.learn.crypto.util.HashingUtil;
import com.learn.crypto.util.MiscUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUser {

	public AppUser() {
		userId = -1;
	}

	public AppUser(final String userName, final String displayName, final String plainTxtPswd) {
		userId = -1;
		this.userName = userName;
		this.displayName = displayName;
		this.setPlainTextPswd( plainTxtPswd ); // this will set(if not already) / use encSalt, set encPswd

		roles = new ArrayList<>();
		Map<String,String> roleA = new HashMap<>();
		roleA.put( "A", "A1" );
		Map<String,String> roleB = new HashMap<>();
		roleB.put( "B", "B1" );
		roles.add( roleA );
		roles.add( roleB );

		others = new HashMap<>();
		others.put( "someKey", "someVal" );
	}

	public boolean SerializeToDb ( Connection conn, int loginUserId, String[] msg ) throws SQLException, IOException {
		boolean bSuccess = true;

		if ( userId<=0 )  // this is a new entry. call doInsert
		{
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				StringBuilder sqlInsertStmt = new StringBuilder()
					.append("INSERT INTO appUser ")
					.append("  ( userName, encPswd, encSalt, firstName, lastName, displayName, emailAddr, phoneNumber, " )
					.append("    createdBy, creationDate, lastUpdatedBy, lastUpdateDate, lastLoginDate, " )
					.append("    roles, logins, others ) ")
					.append("VALUES " )
					.append("  ( ?, ?, ?, ?, ?, ?, ?, ?, ")
					.append("    ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?," )
					.append("	 CAST(? AS jsonb), CAST(? AS jsonb), CAST(? AS jsonb) ) ");
				stmt = (PreparedStatement) conn.prepareStatement( sqlInsertStmt.toString() );
				stmt.setString(1, userName);
				stmt.setString(2, encPswd);
				stmt.setString(3, encSalt);
				stmt.setString(4, firstName);
				stmt.setString(5, lastName);
				stmt.setString(6, displayName);
				stmt.setString(7, emailAddr);
				stmt.setString(8, phoneNumber);
				stmt.setInt(9, loginUserId);
				stmt.setInt(10, loginUserId);
				stmt.setTimestamp(11, lastLoginDate);

				final ObjectMapper mapper = new ObjectMapper();
				PGobject jsonObject = new PGobject();
				jsonObject.setType("text");

				if ( roles!=null ) {
					jsonObject.setValue(mapper.writeValueAsString(roles));
					stmt.setObject(12, jsonObject);
				}
				else {
					stmt.setObject(12, null);
				}

				if ( logins!=null ) {
					jsonObject.setValue(mapper.writeValueAsString(logins));
					stmt.setObject(13, jsonObject);
				}
				else {
					stmt.setObject(13, null);
				}

				if ( others!=null ) {
					jsonObject.setValue(mapper.writeValueAsString(others));
					stmt.setObject(14, jsonObject);
				}
				else {
					stmt.setObject(14, null);
				}
				stmt.executeUpdate();
			}
			finally {
				if (rs != null) {
					try { rs.close(); } catch (SQLException ex) {}
				}
				if (stmt != null) {
					try { stmt.close(); } catch (SQLException ex) {}
				}
			}


		}
		else
		{
			// to be implmented
		}

		return bSuccess;
	}

	private int userId;
	private String userName;
	private String encPswd;
	private String encSalt;
	private String firstName;
	private String lastName;
	private String displayName;
	private String emailAddr;
	private String phoneNumber;
	private int createdBy;
	private Timestamp creationDate;
	private int lastUpdatedBy;
	private Timestamp lastUpdateDate;
	private Timestamp lastLoginDate;
	private List<Object> roles;
	private List<Object> logins;
	private Map<String, Object> others;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEncPswd() {
		return encPswd;
	}

	public void setPlainTextPswd(String plainTxtPswd) {
		if ( this.encSalt==null || this.encSalt.equals("") ) {
			this.encSalt = MiscUtil.getRandomString(12);
		}
		this.encPswd = HashingUtil.HMAC_SHA256( plainTxtPswd, this.encSalt );
	}

	public String getEncSalt() {
		return encSalt;
	}

	public void setEncSalt(String encSalt) {
		this.encSalt = encSalt;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public int getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(int lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Timestamp getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Timestamp lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Timestamp getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Timestamp lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public List<Object> getRoles() {
		return roles;
	}

	public void setRoles(List<Object> roles) {
		this.roles = roles;
	}

	public List<Object> getLogins() {
		return logins;
	}

	public void setLogins(List<Object> logins) {
		this.logins = logins;
	}

	public Map<String, Object> getOthers() {
		return others;
	}

	public void setOthers(Map<String, Object> others) {
		this.others = others;
	}

	@Override
	public String toString() {
		return "AppUser{" +
				"userId=" + userId +
				", userName='" + userName + '\'' +
				", encPswd='" + encPswd + '\'' +
				", encSalt='" + encSalt + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", displayName='" + displayName + '\'' +
				", emailAddr='" + emailAddr + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", createdBy=" + createdBy +
				", creationDate=" + creationDate +
				", lastUpdatedBy=" + lastUpdatedBy +
				", lastUpdateDate=" + lastUpdateDate +
				", lastLoginDate=" + lastLoginDate +
				", roles='" + roles + '\'' +
				", logins='" + logins + '\'' +
				", others='" + others + '\'' +
				'}';
	}
}
