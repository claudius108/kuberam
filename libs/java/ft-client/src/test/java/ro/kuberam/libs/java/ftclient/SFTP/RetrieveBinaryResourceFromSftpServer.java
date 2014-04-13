package ro.kuberam.libs.java.ftclient.SFTP;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Connect;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.RetrieveResource;

import com.jcraft.jsch.Session;

public class RetrieveBinaryResourceFromSftpServer extends FTClientAbstractTest {

	@Test
	public void retrieveBinaryResourceFromSftpServer() throws Exception {

		Session connection = Connect.connect(new URI(connectionProperties.getProperty("sftp-server-connection-url")),
				getBinaryResourceAsString("../resources/Open-Private-Key"));

		String actualResult = getTextContent(RetrieveResource.retrieveResource(connection, "/home/ftp-user/dir-with-rights/image-with-rights.gif"));

		Disconnect.disconnect(connection);

		String expectedResult = getBinaryResourceAsBase64String("../image-with-rights.gif");

		Assert.assertTrue(expectedResult.equals(actualResult));
	}
}
