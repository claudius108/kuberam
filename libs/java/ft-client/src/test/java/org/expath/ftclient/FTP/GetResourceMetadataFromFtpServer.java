package org.expath.ftclient.FTP;

import java.net.URI;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.expath.ftclient.Connect;
import org.expath.ftclient.Disconnect;
import org.expath.ftclient.FTClientAbstractTest;
import org.junit.Assert;
import org.junit.Test;

public class GetResourceMetadataFromFtpServer extends FTClientAbstractTest {

	@Test
	public void getResourceMetadataFromFtpServer() throws Exception {
		Properties connectionProperties = new Properties();
		connectionProperties.load(this.getClass().getResourceAsStream("../connection.properties"));

		FTPClient connection = Connect.connect(new URI(connectionProperties.getProperty("ftp-server-connection-url")), "");

		String actualResult = connection.getStatus("/dir-with-rights/test.txt");
		
		System.out.println(actualResult);
				//getTextContent(RetrieveResource.retrieveResource(connection, "/dir-with-rights/test.txt"));

		Disconnect.disconnect(connection);

		String expectedResult = getBinaryResourceAsBase64String("../resources/test.txt");

		Assert.assertTrue(expectedResult.equals(actualResult));
	}
}
