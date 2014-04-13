package org.expath.ftclient.SFTP;

import java.net.URI;
import java.util.Properties;

import org.expath.ftclient.Connect;
import org.expath.ftclient.Disconnect;
import org.expath.ftclient.FTClientAbstractTest;
import org.expath.ftclient.RetrieveResource;
import org.junit.Assert;
import org.junit.Test;

import com.jcraft.jsch.Session;

public class RetrieveTextResourceFromSftpServer extends FTClientAbstractTest {

	@Test
	public void retrieveTextResourceFromSftpServer() throws Exception {
		Properties connectionProperties = new Properties();
		connectionProperties.load(this.getClass().getResourceAsStream("../connection.properties"));

		Session connection = Connect.connect(new URI(connectionProperties.getProperty("sftp-server-connection-url")),
				getBinaryResourceAsString("../resources/Open-Private-Key"));

		String actualResult = getTextContent(RetrieveResource.retrieveResource(connection, "/home/ftp-user/dir-with-rights/test.txt"));

		Disconnect.disconnect(connection);

		String expectedResult = getBinaryResourceAsBase64String("../resources/test.txt");

		Assert.assertTrue(expectedResult.equals(actualResult));
	}
}
