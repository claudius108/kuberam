package ro.kuberam.libs.java.crypto.junit.digest;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import ro.kuberam.libs.java.crypto.digest.Hmac;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.tests.junit.BaseTest;

public class HmacStringWithSha1AndDefaultProvider extends BaseTest {

	@Test
	public void hmacStringWithSha1() throws Exception {
		String input = "abc";
//		InputStream secretKeyIs = getClass().getResourceAsStream("../../resources/private-key.pem");
		
		String result = Hmac.hmac(input, "def", "HMAC-SHA-1", "");
		
		System.out.println(result);

		Assert.assertTrue(result
				.equals("55LyDq7GFnqijauK4CQWR4AqyZk="));
	}
}
