package ro.kuberam.libs.java.ftclient.SFTP;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Connect;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.ListResources;

import com.jcraft.jsch.Session;

public class ListResourcesTest extends FTClientAbstractTest {

	@Test
	public void listResourcesFromSftpServer() throws Exception {

		Session connection = Connect.connect(
				new URI(connectionProperties.getProperty("sftp-server-connection-url")),
				getBinaryResourceAsString("../sftp-private.key"));

		String actualResult = serializeToString(ListResources.listResources(connection, sftpHomeDirPath
				+ "/"));

		Disconnect.disconnect(connection);

		String expectedResult = "image-with-rights.gif";

		Assert.assertTrue(actualResult.contains(expectedResult));
	}
}
