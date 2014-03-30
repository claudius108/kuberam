package org.expath.crypto.digest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.expath.crypto.ErrorMessages;
import org.expath.crypto.ExpathCryptoModule;
import org.expath.crypto.utils.Base64;

public class Hmac {

	private final static Logger log = Logger.getLogger(Hmac.class);

	public static String hmac(String data, String secretKey, String algorithm, String format)
			throws Exception {

		// TODO: validate the format
		format = format.equals("") ? "base64" : format;

		byte[] encodedKey = null;
		byte[] encodedData = null;
		StringBuffer sb = null;
		Mac mac = null;
		HashMap<String, String> javaStandardAlgorithmNames = ExpathCryptoModule.javaStandardAlgorithmNames;

		if (javaStandardAlgorithmNames.containsKey(algorithm)) {
			algorithm = javaStandardAlgorithmNames.get(algorithm);
		}

		// encoding the key
		try {
			encodedKey = secretKey.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}

		// generating the signing key
		SecretKeySpec signingKey = new SecretKeySpec(encodedKey, algorithm);

		// get and initialize the Mac instance
		try {
			mac = Mac.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(ErrorMessages.err_CX21);
		}

		try {
			mac.init(signingKey);
		} catch (InvalidKeyException ex) {
		}

		// encode the data
		try {
			encodedData = data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}

		// compute the hmac
		byte[] resultBytes = mac.doFinal(encodedData);

		// get the result
		if (format.equals("base64")) {
			return Base64.encodeToString(resultBytes, true);
		} else {
			BigInteger bigInt = new BigInteger(1, resultBytes);

			return bigInt.toString(16);
		}
	}
}
