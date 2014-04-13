package org.expath.ftclient.SFTP;

import java.net.URI;
import java.util.Properties;

import org.expath.ftclient.Connect;
import org.expath.ftclient.Disconnect;
import org.expath.ftclient.FTClientAbstractTest;
import org.expath.ftclient.ListResources;
import org.junit.Assert;
import org.junit.Test;

import com.jcraft.jsch.Session;

public class ListResourcesFromSftpServer extends FTClientAbstractTest {

	@Test
	public void listResourcesFromSftpServer() throws Exception {
		Properties connectionProperties = new Properties();
		connectionProperties.load(this.getClass().getResourceAsStream("../connection.properties"));

		Session connection = Connect.connect(new URI(connectionProperties.getProperty("sftp-server-connection-url")),
				getBinaryResourceAsString("../resources/Open-Private-Key"));

		String actualResult = serializeToString(ListResources.listResources(connection, "/home/ftp-user/dir-with-rights"));

		Disconnect.disconnect(connection);

		String expectedResult = "image-with-rights.gif";

		Assert.assertTrue(actualResult.contains(expectedResult));
	}
}
