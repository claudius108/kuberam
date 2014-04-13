package ro.kuberam.libs.java.ftclient.FTP;

import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import ro.kuberam.libs.java.ftclient.Connect;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.ListResources;
import org.junit.Assert;
import org.junit.Test;

public class ListResourcesTest extends FTClientAbstractTest {

	@Test
	public void listResourcesFromFtpServer() throws Exception {
		Properties connectionProperties = new Properties();
		connectionProperties.load(this.getClass().getResourceAsStream("../connection.properties"));

		FTPClient connection = Connect.connect(
				new URI(connectionProperties.getProperty("ftp-server-connection-url")), "");

		String actualResult = serializeToString(ListResources
				.listResources(connection, "/dir-with-rights/"));

		Disconnect.disconnect(connection);

		String expectedResult = "image-with-rights.gif";

		Assert.assertTrue(actualResult.contains(expectedResult));
	}

	@Test
	public void complexTest() throws Exception {

		InputStream resource = this.getClass().getResourceAsStream("../image-with-rights.gif");

		FTPClient connection = Connect.connect(new URI("ftp://127.0.0.1"), "");

		// Boolean storeResourceResult = StoreResource.storeResource(connection,
		// "/VOLUME1/FTP/nosql.sync-io.net/test.gif", resource);

		String listResourcesResult = serializeToString(ListResources.listResources(connection,
				"/VOLUME1/FTP/nosql.sync-io.net/"));

		// String retrieveResourceResult =
		// getTextContent(RetrieveResource.retrieveResource(connection,
		// "/VOLUME1/FTP/nosql.sync-io.net/test.gif"));
		//
		// Boolean deleteResourceResult =
		// DeleteResource.deleteResource(connection,
		// "/VOLUME1/FTP/nosql.sync-io.net/test.gif");

		Disconnect.disconnect(connection);

		// String expectedRetrieveResourceResult =
		// getBinaryResourceAsBase64String("../image-with-rights.gif");

		System.out.println("listResourcesResult: " + listResourcesResult + "\n");
		// System.out.println("storeResourceResult: " + storeResourceResult +
		// "\n");
		// System.out.println("retrieveResourceResult: "
		// + expectedRetrieveResourceResult.equals(retrieveResourceResult) +
		// "\n");
		// System.out.println("deleteResourceResult: " + deleteResourceResult +
		// "\n");
	}
}
