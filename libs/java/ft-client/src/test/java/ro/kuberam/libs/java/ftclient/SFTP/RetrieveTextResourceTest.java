package ro.kuberam.libs.java.ftclient.SFTP;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Connect;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.RetrieveResource;

import com.jcraft.jsch.Session;

public class RetrieveTextResourceTest extends FTClientAbstractTest {

	@Test
	public void retrieveTextResourceFromSftpServer() throws Exception {

		Session connection = Connect.connect(new URI(connectionProperties.getProperty("sftp-server-connection-url")),
				getBinaryResourceAsString("../sftp-private.key"));

		String actualResult = getBinaryResourceAsBase64String(RetrieveResource.retrieveResource(connection, sftpHomeDirPath + "/test.txt"));

		Disconnect.disconnect(connection);

		String expectedResult = getBinaryResourceAsBase64String("../test.txt");

		Assert.assertTrue(expectedResult.equals(actualResult));
	}
}
