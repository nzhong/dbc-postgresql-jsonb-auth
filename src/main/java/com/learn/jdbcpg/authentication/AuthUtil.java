package com.learn.jdbcpg.authentication;

import com.learn.crypto.util.HashingUtil;
import com.learn.jdbcpg.model.service.AppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;

public class AuthUtil {
	private static Logger log = LoggerFactory.getLogger(AuthUtil.class);

	/**
	 * For reference please see http://jasypt.org/howtoencryptuserpasswords.html
	 *
	 * I. Encrypt passwords using one-way techniques, this is, digests.
	 *   We use HMAC_SHA256 as our main algorithm. It's non-reversible,
	 *   i.e NO "Retrieve Password". ONLY "Reset Password"
	 * II. Match input and stored passwords by comparing digests, not unencrypted strings.
	 *   Sure.
	 * III. Improving the security of our digests, use Variable Salt
	 *   For each user record we generate and store a different string as encSalt.
	 *   We use this encSalt to calculate the digest.
	 */
	public static int validate(final Connection conn, final String userName, final String plainTxtPswd) {
		Map<String,String> found = AppUserService.getUserCredentialByUserName(conn, userName);
		if ( found==null ) {
			log.info("userName not found.");
			return -1;
		}

		int userId = Integer.parseInt( found.get("userId") );
		String encPswdStoredInDB = found.get("encPswd");  // this is the encrypted pswd stored in DB
		String encSalt = found.get("encSalt");
		String calculatedEncPswd = HashingUtil.HMAC_SHA256( plainTxtPswd, encSalt ); // calculated from user input
		if ( calculatedEncPswd!=null && calculatedEncPswd.equals(encPswdStoredInDB) && userId>0 ) {
			log.debug("PSWD check passed.");
			return userId;
		}
		else{
			log.warn( "PSWD check FAILED. userId = {}, encPswdStoredInDB={}, encPswdCalc={}", userId, encPswdStoredInDB, calculatedEncPswd);
			return -1;
		}
	}
}
