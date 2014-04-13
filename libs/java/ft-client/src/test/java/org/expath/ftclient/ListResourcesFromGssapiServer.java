package org.expath.ftclient;

import java.net.URI;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.expath.ftclient.Connect;
import org.expath.ftclient.Disconnect;
import org.expath.ftclient.ListResources;
import org.junit.Assert;
import org.junit.Test;

import com.jcraft.jsch.Session;

public class ListResourcesFromGssapiServer extends FTClientAbstractTest {

	private String folderPath = "/20140121000000001/0617353648546/resources/";
	private String[] fileNames = new String[] { "0617353648546.jpg", "0617353648546_1_1.flac",
			"0617353648546_1_2.flac", "0617353648546_1_3.flac", "0617353648546_1_4.flac" };
	private String sftpPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n"
			+ "MIIEowIBAAKCAQEAzeBHfoFE4ourVcDrhWJh3Sh1zxqmz87R6et9aWC4xvSKIs/+\n"
			+ "uw8fDRwgT09ElOln6tQKYITI1Cw5LBs3fQELdcksHX5N7JVi/1MzOGR89V4Zdlw2\n"
			+ "n1DMsC6teRbOVtEC6iin90lRepfAUHzWOf5AQcSIYAtAj80AgB3w7zYFUDMlx4bJ\n"
			+ "0fB9IkVEHu3SIxvgmp6NvqlkmJ0jLdKaeNc6MsdmPjzONyxlGcsaQ8SvpP49Qkt+\n"
			+ "rA43qrXbQa84DvurGxcrsmBpRCl+cyGJMKF1nNL0sbXodyjpKOQEWovOT48meA0u\n"
			+ "Liw0mc5lg+/jmmFcrfUgt8JhOcm1eRysQvVy1QIBIwKCAQEAtlju3cLz3pj21uyz\n"
			+ "WOIcKkhoWFlvLRZE5SEIrcqySdFHJiXwP0foTXCu5yjzmdYLlXn6kACUnqrTjXcx\n"
			+ "JZM2CT0nEs70j7eDkbAXbHZCyrJuUuP13ZC1S5cOrRQzGbHPYa77K3t0DX8YDMZX\n"
			+ "V+/Sg2Tt0WkNTCqaCxM0fBnncunhuyKUV886P3URbmh5LnMrfOSgLiJ2FRKQLwaX\n"
			+ "e7whGZRGvggCCEzFm6caIrLKwZaJYrUPepX83ZBDz+b1snG2s8RxbjKZFfw300mN\n"
			+ "dNAPo5SYz+BvmFSfRtAk9seyJOc6lLrNXbVHvS+YrCqASM8siE3T1eWuh5jAW+ZN\n"
			+ "Y/lGRwKBgQD0su1kO/6xzeeWqI0sfBUDNKEU2VFgTKcfa12d9u+DsVDpHmR3YqDj\n"
			+ "q7gs5Ec5STPgwE8wXmkowlJ0PgLA1YVGrm3ECfOjl1x/T+otwLRKIHrfyL/Dlimc\n"
			+ "YhLOJZk08BEImfXbxry1dcWn30BFHlsRxMENT8kcEQUJcO9sag0rMwKBgQDXYlmU\n"
			+ "z7U/PkTb2LDjBzqDAkWrRebJTPGShRr2o5+yKsbSalNtT02aEOroo6tXokFu0Ho0\n"
			+ "kbFlBwKWUsB6mUiWJdPfVUxrarA7+twOTyNLYI9HWRnH6rBGXhfCJpVD2v0uTvHV\n"
			+ "5NyhwgzPNOK7EQve03DZ6hb7+wex0feuqhkp1wKBgQC8xIs+sfBdRxG9XXQ4Qnac\n"
			+ "EqgmBrseZwSUlKdN+QHpP6TRF3IS9FA6mmmBt2ok4LL91i51xSyNKDD6ljyjYuMn\n"
			+ "5aUiM5AQfBQnsq1Wfrb3WuJyHoVNvPuHRFemV4TREPcyhWXkDlcPo/7glkeFxvXL\n"
			+ "4Oyyf2Cgn2pJHJtw5BjJjQKBgGide/fSmdzjuwuyZIuHKwxu0WHRfrmLxcrS79bh\n"
			+ "wphd64rF7gHksKnq9cF0EWUUS6raSfxGw+CdAUGzKkotMeKHZujRqMZ1pg6BKQ5D\n"
			+ "sg6rPkc55/NjXO76gJGAdGLJc6jHQkNR54HEpyLQi2Is2eFCIN7faj/gVDHMYlwm\n"
			+ "u8PdAoGBAMZPswg8s0rlwbYKUqDw2ucufUB7CNDJg78tZgSHkDSTyStANG9jZ0Sk\n"
			+ "LtJdQDBvRStV02v7rKGZTxbE6722uSc0sLLIAgW5ihf5STmz2Oc2lLWbzPl7uDp3\n"
			+ "xcGFC4rK9cGeTsOt9tf9/0omdWW9tnO39dQz2N9pEpoo1ZLTnRYw\n" + "-----END RSA PRIVATE KEY-----";

	@Test
	public void listResourcesFromGssapiServer() throws Exception {
		Properties connectionProperties = new Properties();
		connectionProperties.load(this.getClass().getResourceAsStream("../connection.properties"));

		Session connection = Connect.connect(new URI("sftp://narm-ftp:narm-pass@50.17.208.135"),
				getBinaryResourceAsString("50-17-208-135-rsa-private-key"));

		String actualResult = getTextContent(ListResources.listResources(connection, "/CI"));

		Disconnect.disconnect(connection);

		System.out.println("actualResult: " + actualResult);

		String expectedResult = getBinaryResourceAsBase64String("../resources/test.txt");

		Assert.assertTrue(expectedResult.equals(actualResult));
	}

	@Test
	public void downloadLargeFilesFromRemoteFtpServer() throws Exception {
		FTPClient connection = Connect.connect(new URI("ftp://nueftp41:nuemeta222@50.17.208.135"), "");

		for (String fileName : fileNames) {
			RetrieveResource.retrieveResource(connection, folderPath + fileName);
		}

		// String actualResult =
		// getTextContent(RetrieveResource.retrieveResource(connection,
		// "/20140121000000001/0617353648546/resources/0617353648546.jpg"));

		Disconnect.disconnect(connection);

		// System.out.println("actualResult: " + actualResult);
	}

	@Test
	public void downloadLargeFilesFromRemoteSftpServer() throws Exception {
		Session connection = Connect.connect(new URI("sftp://nuedata@50.17.208.135"), sftpPrivateKey);

		for (String fileName : fileNames) {
			RetrieveResource.retrieveResource(connection, folderPath + fileName);
		}

		// String actualResult =
		// getTextContent(RetrieveResource.retrieveResource(connection,
		// "/20140121000000001/0617353648546/resources/0617353648546.jpg"));

		Disconnect.disconnect(connection);

		// System.out.println("actualResult: " + actualResult);
	}

	@Test
	public void downloadLargeFilesFromLocalFtpServer() throws Exception {
		FTPClient connection = Connect.connect(new URI("ftp://ftp-user:ftp-pass@127.0.0.1"), "");

		for (String fileName : fileNames) {
			RetrieveResource.retrieveResource(connection, folderPath + fileName);
		}

		// String actualResult =
		// getTextContent(RetrieveResource.retrieveResource(connection,
		// "/20140121000000001/0617353648546/resources/0617353648546.jpg"));

		Disconnect.disconnect(connection);

		// System.out.println("actualResult: " + actualResult);
	}
}
