package com.smallcat.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashKit.java
 * ��ɢ�й���
 * 
 * @version 1.0
 * @author SUNY
 * Written Date: 2008-2-3
 * 
 * Modified By: 
 * Modified Date:
 */
public class HashKit {

	private Coder coder = new Base64Coder();
	/**ȱʡ�ַ���*/
	private static final String charsetName = "GBK";

	/**
	 * HMAC
	 * @param text
	 * @param key
	 * @return
	 * @throws GeneralSecurityException
	 */
	public String hmac(String value, String key) throws GeneralSecurityException {
		Hmac hamc = new Hmac();
		byte[] hash = hamc.hmac(getBytes(value), getBytes(key));
		return coder.encode(hash);
	}

	/**
	 * SHA-1ɢ�У�base64���룩
	 * @param value
	 * @return
	 */
	public String digestSHA1(String value) {
		byte[] hash = digestSHA1(getBytes(value));
		return coder.encode(hash);
	}

	/**
	 * SHA-1ɢ�У�base64���룩
	 * @param stream ������(���÷�����ر�)
	 * @return
	 * @throws IOException 
	 */
	public String digestSHA1(InputStream stream) throws IOException {
		try {
			MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");
			byte[] buf = new byte[1024];
			int len;
			while ((len = stream.read(buf)) > 0) {
				messagedigest.update(buf, 0, len);
			}
			return coder.encode(messagedigest.digest());
		}
		catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * �ֽ�����base64������ַ���
	 * @param something
	 * @return
	 */
	public String encode64(byte[] something) {
		return coder.encode(something);
	}
	
	/**
	 * SHA-1ɢ��
	 * @param value
	 * @return
	 */
	public byte[] digestSHA1(byte[] value) {
		try {
			MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");
			return messagedigest.digest(value);
		}
		catch(NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private final byte[] getBytes(String str) {
		try {
			return str.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			return str.getBytes();
		}
	}
}
