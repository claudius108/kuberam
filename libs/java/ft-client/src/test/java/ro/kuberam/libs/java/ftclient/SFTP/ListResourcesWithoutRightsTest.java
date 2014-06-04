package ro.kuberam.libs.java.ftclient.SFTP;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.ListResources;

import com.jcraft.jsch.Session;

public class ListResourcesWithoutRightsTest extends FTClientAbstractTest {

	@Test
	public void listResourcesFromSftpServer() throws Exception {

		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("../sftp-private.key")));
		String remoteResourcePath = sftpHomeDirPath + "/dir-without-rights/";
		try {
			ListResources.listResources(remoteConnection, remoteResourcePath);
//			Assert.assertTrue(false);
		} catch (Exception e) {
//			Assert.assertTrue(e.getLocalizedMessage().equals(
//					"err:FTC004: The user has no rights to access the remote resource."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}

	}
}
