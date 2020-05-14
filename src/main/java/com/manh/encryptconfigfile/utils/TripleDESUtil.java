package com.manh.encryptconfigfile.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Setter;

@Setter
@Component
@ConfigurationProperties(prefix = "file.encryption", ignoreUnknownFields = false)
public class TripleDESUtil {

	String key;

	/**
	 * Method To Encrypt The String
	 *
	 * @param unencryptedString
	 * @return encrpted string
	 * @throws java.security.NoSuchAlgorithmException
	 * @throws java.io.UnsupportedEncodingException
	 * @throws javax.crypto.NoSuchPaddingException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.IllegalBlockSizeException
	 * @throws javax.crypto.BadPaddingException
	 */
	public String harden(String unencryptedString) throws NoSuchAlgorithmException, UnsupportedEncodingException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		MessageDigest md = MessageDigest.getInstance("md5");
		byte[] digestOfPassword = md.digest(key.getBytes("utf-8"));
		byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}

		SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		byte[] plainTextBytes = unencryptedString.getBytes("utf-8");
		byte[] buf = cipher.doFinal(plainTextBytes);
		byte[] base64Bytes = Base64.encodeBase64(buf);
		String base64EncryptedString = new String(base64Bytes);

		return base64EncryptedString;
	}

	/**
	 * Method To Decrypt An Ecrypted String
	 *
	 * @param encryptedString
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 * @throws java.security.NoSuchAlgorithmException
	 * @throws javax.crypto.NoSuchPaddingException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.IllegalBlockSizeException
	 * @throws javax.crypto.BadPaddingException
	 */
	public String soften(String encryptedString) throws UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		if (encryptedString == null) {
			return "";
		}
		byte[] message = Base64.decodeBase64(encryptedString.getBytes("utf-8"));

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digestOfPassword = md.digest(key.getBytes("utf-8"));
		byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}

		SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");

		Cipher decipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		decipher.init(Cipher.DECRYPT_MODE, secretKey);

		byte[] plainText = decipher.doFinal(message);

		return new String(plainText, "UTF-8");

	}

}