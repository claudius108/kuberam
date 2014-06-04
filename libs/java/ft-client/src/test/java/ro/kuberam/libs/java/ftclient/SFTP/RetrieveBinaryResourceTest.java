package ro.kuberam.libs.java.ftclient.SFTP;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Connect;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.RetrieveResource;

import com.jcraft.jsch.Session;

public class RetrieveBinaryResourceTest extends FTClientAbstractTest {

	@Test
	public void retrieveBinaryResourceFromSftpServer() throws Exception {

		Session connection = Connect.connect(
				new URI(connectionProperties.getProperty("sftp-server-connection-url")),
				getBinaryResourceAsString("../sftp-private.key"));

		String actualResult = getBinaryResourceAsBase64String(RetrieveResource.retrieveResource(connection,
				sftpHomeDirPath + "/image-with-rights.gif"));

		Disconnect.disconnect(connection);

		String expectedResult = getBinaryResourceAsBase64String("../image-with-rights.gif");

		Assert.assertTrue(expectedResult.equals(actualResult));
	}
}
