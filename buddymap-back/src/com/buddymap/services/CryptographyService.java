package com.buddymap.services;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.buddymap.model.Authentication;


public class CryptographyService {
	
	private static Logger logger = Logger.getRootLogger();
	private static final String HASH_ALGO = "SHA-512";
	private static final String HMAC_ALGO = "HmacSHA256";
	private static final String CRYPTOGRAPHIC_ALGO = "AES";
	
	public static String hashSaltedSHA512(String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(HASH_ALGO);
	        md.update(password.getBytes());
	        
	        byte rawHash[] = md.digest();
	 
	        // base64-encode the hmac
	     	String result = DatatypeConverter.printBase64Binary(rawHash);
	     	return result;
		} catch (NoSuchAlgorithmException e) {
			logger.error("Unable to find algorithm", e);
		}
		return null;
	}

	public static boolean checkSignature(Authentication authent,
			Authentication storedAuthent) throws SignatureException {
		//Checking of the client timestamp
		if(authent.getClientTimestamp()==storedAuthent.getClientTimestamp() || authent.getClientTimestamp() < storedAuthent.getClientTimestamp()){
			return false;
		}
		String generatedHash = null;
		//Generating the hash with the algo specified
		switch(authent.getHashAlgorythm()){
			case "SHA256" : generatedHash = generateHMACSHA256(authent.getMail()+authent.getClientTimestamp()+authent.getHashAlgorythm(), storedAuthent.getEncryptedToken());
			break;
		}
		//Testing the hash generated and the value given by the client
		if(authent.getHashSignature() != null && authent.getHashSignature().equalsIgnoreCase(generatedHash)){
			return true;
		}
		return false;
	}

	public static String generateCryptedToken(String login) {
		Long serverTimestamp = new Date().getTime();
		//TODO : Check l'impact que peut avoir le fait d'avoir deux timestamp server différents
		return encryptWithAES(login+serverTimestamp);
	}

	public static String generateHMACSHA256(String data, String key) throws SignatureException{
		String result = null;
		try {
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),HMAC_ALGO);
			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_ALGO);
			mac.init(signingKey);
			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());
			result = DatatypeConverter.printHexBinary(rawHmac);

		} catch (NoSuchAlgorithmException e) {
			logger.error("Unable to find algorithm", e);
		} catch (InvalidKeyException e) {
			logger.error("Invalid key", e);
		}
		return result;
	}
	
	public static String encryptWithAES(String pwd) {
		byte[] cipherText = null;
	    try {
	    	Key privateKey = getKey();
	    	// get an AES cipher object
	    	final Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGO);
	    	// encrypt the plain text using the public key
	    	cipher.init(Cipher.ENCRYPT_MODE, privateKey);
	    	cipherText = cipher.doFinal(pwd.getBytes());	      
	    } catch (Exception e) {
	    	//TODO
	    	e.printStackTrace();
	    }
	    return DatatypeConverter.printBase64Binary(cipherText);
	}
	
	public static String decryptWithAES(String pwd) {
		byte[] decryptedText = null;
	    try {
	    	Key privateKey = getKey();
	    	// get an RSA cipher object and print the provider
	    	final Cipher cipher = Cipher.getInstance(CRYPTOGRAPHIC_ALGO);
	    	// encrypt the plain text using the public key
	    	cipher.init(Cipher.DECRYPT_MODE, privateKey);
	    	decryptedText = cipher.doFinal(pwd.getBytes());
	    } catch (Exception e) {
	    	//TODO
	    }
	    return new String(decryptedText);
	}
	
	private static Key getKey(){
		byte[] privateKey = null;
		try {
			privateKey = PropertiesLoader.getInstance().getAESKey().getBytes("UTF-8");
			privateKey = Arrays.copyOf(privateKey, 16); // use only first 128 bit
			Key key = new SecretKeySpec(privateKey, CRYPTOGRAPHIC_ALGO); 
			return key;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
