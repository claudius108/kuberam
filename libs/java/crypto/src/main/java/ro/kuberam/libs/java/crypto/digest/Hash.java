/*
 *  Copyright (C) 2011 Claudius Teodorescu
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */

package org.expath.crypto.digest;

/**
 * Implements the crypto:hash() function.
 * 
 * @author Claudius Teodorescu <claudius.teodorescu@gmail.com>
 */

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.expath.crypto.ErrorMessages;
import org.expath.crypto.utils.Base64;

public class Hash {

	private final static Logger log = Logger.getLogger(Hash.class);
	private final static String inputStringEncoding = "UTF-8";

	public static String hashString(String data, String algorithm, String format) throws Exception {

		// TODO: validate the format
		// format = format.equals("") ? "SUN" : format;

		MessageDigest messageDigester = getMessageDigester(algorithm);

		messageDigester.update(data.getBytes(inputStringEncoding));

		byte[] resultBytes = messageDigester.digest();

		if (format.equals("base64")) {
			return Base64.encodeToString(resultBytes, true);
		} else {
			BigInteger bigInt = new BigInteger(1, resultBytes);

			return bigInt.toString(16);
		}
	}

	public static String hashBinary(InputStream data, String algorithm, String format) throws Exception {

		// TODO: validate the format
		format = format.equals("") ? "base64" : format;

		BufferedInputStream bis = new BufferedInputStream(data);
		MessageDigest messageDigester = getMessageDigester(algorithm);
		DigestInputStream dis = new DigestInputStream(bis, messageDigester);

		while (dis.read() != -1)
			;

		byte[] resultBytes = messageDigester.digest();

		if (format.equals("base64")) {
			return Base64.encodeToString(resultBytes, true);
		} else {
			BigInteger bigInt = new BigInteger(1, resultBytes);

			return bigInt.toString(16);
		}

		// byte[] buffer = new byte[bufferSize];
		// int sizeRead = -1;
		// while ((sizeRead = in.read(buffer)) != -1) {
		// digest.update(buffer, 0, sizeRead);
		// }
		// in.close();
		//
		// byte[] hash = null;
		// hash = new byte[digest.getDigestLength()];
		// hash = digest.digest();
	}

	private static MessageDigest getMessageDigester(String algorithm) throws Exception {
		MessageDigest messageDigester = null;

		try {
			messageDigester = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(ErrorMessages.err_CX21);
		}

		return messageDigester;
	}
}