package ro.kuberam.libs.java.crypto.junit.digest;

import java.io.InputStream;

import ro.kuberam.libs.java.crypto.digest.Hash;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.tests.junit.BaseTest;

public class HashLargeBinaryWithMd5 extends BaseTest {

	@Test
	public void hashLargeBinaryWithMd5() throws Exception {
		InputStream input = getClass().getResourceAsStream("../../resources/012886100224_01_01.flac");
		String result = Hash.hashBinary(input, "MD5", "SUN");
		
		System.out.println(result);

		Assert.assertTrue(result.equals("ufUQavUYKXqjisb14jMwNw=="));
	}
}
