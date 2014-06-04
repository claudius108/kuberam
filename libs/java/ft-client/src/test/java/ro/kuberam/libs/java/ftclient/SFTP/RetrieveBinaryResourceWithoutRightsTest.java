package ro.kuberam.libs.java.ftclient.SFTP;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.RetrieveResource;

import com.jcraft.jsch.Session;

public class RetrieveBinaryResourceWithoutRightsTest extends FTClientAbstractTest {

	@Test
	public void listResourcesFromSftpServer() throws Exception {

		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("../sftp-private.key")));
		String remoteResourcePath = sftpHomeDirPath + "/image-no-rights.gif";
		try {
			RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath);
			// Assert.assertTrue(false);
		} catch (Exception e) {
			// Assert.assertTrue(e.getLocalizedMessage().equals(
			// "err:FTC004: The user has no rights to access the remote resource."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}

	}
}
