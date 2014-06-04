package ro.kuberam.libs.java.ftclient.SFTP;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.RetrieveResource;

import com.jcraft.jsch.Session;

public class RetrieveNonExistingBinaryResourceTest extends FTClientAbstractTest {

	@Test
	public void listResourcesFromSftpServer() throws Exception {

		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("../sftp-private.key")));
		String remoteResourcePath = sftpHomeDirPath + "/non-existing-image.gif";
		try {
			RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC003: The remote resource does not exist."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
		
	}
}
