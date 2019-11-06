package pl.dentistoffice.mobile.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service
public class CipherService {

	private String encryptionKey;

	public CipherService(Environment env) {
		this.encryptionKey = env.getProperty("encryptionKey");
		
//		for Heroku cloud
//		this.encryptionKey = System.getenv("ENCRYPTION_KEY");
	}

	public String encodeToken(String token) {
		
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte [] byteKey = encryptionKey.getBytes("UTF-8");
			byteKey = Arrays.copyOf(byteKey, 16);
			Key secretKey = new SecretKeySpec(byteKey, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
			byte[] encodeTokenByte = cipher.doFinal(token.getBytes());
			return Base64.getEncoder().encodeToString(encodeTokenByte);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public String decodeToken(String encodeTokenBase64) {
//
//		byte[] encodeTokenByte = null;
//
//		if (encodeTokenBase64 != null && !encodeTokenBase64.equals("")) {
//			encodeTokenByte = Base64.getDecoder().decode(encodeTokenBase64);
//			try {
//				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//				byte[] byteKey = encryptionKey.getBytes("UTF-8");
//				byteKey = Arrays.copyOf(byteKey, 16);
//				Key secretKey = new SecretKeySpec(byteKey, "AES");
//				cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
//				byte[] decodeTokenByte = cipher.doFinal(encodeTokenByte);
//				return new String(decodeTokenByte);
//			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
//				e.printStackTrace();
//			} catch (InvalidKeyException e) {
//				e.printStackTrace();
//			} catch (IllegalBlockSizeException e) {
//				e.printStackTrace();
//			} catch (BadPaddingException e) {
//				e.printStackTrace();
//			} catch (InvalidAlgorithmParameterException e) {
//				e.printStackTrace();
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
}
