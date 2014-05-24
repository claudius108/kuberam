package ro.kuberam.libs.java.ftclient.FTP;

import java.net.URI;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Connect;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.ListResources;

public class ListResourcesTest extends FTClientAbstractTest {

	@Test
	public void listResourcesFromFtpServer() throws Exception {

		FTPClient connection = Connect.connect(
				new URI(connectionProperties.getProperty("ftp-server-connection-url")), "");

		String actualResult = serializeToString(ListResources
				.listResources(connection, "/dir-with-rights/"));

		Disconnect.disconnect(connection);

		String expectedResult = "image-with-rights.gif";

		Assert.assertTrue(actualResult.contains(expectedResult));
	}

}
