package com.smallcat.common.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HMAC
 * @author suny
 * 
 */
public class Hmac {
	
	private Log logger = LogFactory.getLog(Hmac.class);
	private String algorithm = "HmacSHA1";

	public Hmac() {
	}

	public Hmac(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * hmac
	 * <p/>
	 */
	public byte[] hmac(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKey sk = new SecretKeySpec(key, algorithm);
		Mac mac = Mac.getInstance(algorithm);
		mac.init(sk);
		logger.info("used hmac");
		return mac.doFinal(data);
	}
}
