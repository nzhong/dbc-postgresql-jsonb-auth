package com.learn.crypto.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HashingUtil {
	private static final Logger log = LoggerFactory.getLogger(HashingUtil.class);

	public static String HMAC_MD5(final String msg, final String keyString) {
		return hmacDigest(msg, keyString, "HmacMD5");
	}
	public static String HMAC_SHA1(final String msg, final String keyString) {
		return hmacDigest(msg, keyString, "HmacSHA1");
	}
	public static String HMAC_SHA256(final String msg, final String keyString) {
		return hmacDigest(msg, keyString, "HmacSHA256");
	}
	private static String hmacDigest(final String msg, final String keyString, final String algo) {
		String digest = null;
		try {
			SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
			Mac mac = Mac.getInstance(algo);
			mac.init(key);

			byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
			StringBuffer hash = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}
			digest = hash.toString();
		} catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException e) {
			log.error( "Exception: ", e );
		}
		return digest;
	}
}
