package ro.kuberam.libs.java.crypto.junit.digest;

import org.apache.commons.io.IOUtils;
import ro.kuberam.libs.java.crypto.digest.Hash;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.tests.junit.BaseTest;

public class HashLargeStringWithMd5 extends BaseTest {

	@Test
	public void hashLargeStringWithMd5() throws Exception {
		String input = IOUtils.toString(getClass().getResourceAsStream("../../resources/012886100224_01_01.flac"));
		String result = Hash.hashString(input, "MD5", "SUN");
		
		System.out.println(result);

		Assert.assertTrue(result.equals("ufUQavUYKXqjisb14jMwNw=="));
	}
}
