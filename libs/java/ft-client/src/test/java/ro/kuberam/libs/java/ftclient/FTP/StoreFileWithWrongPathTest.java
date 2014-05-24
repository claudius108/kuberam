package ro.kuberam.libs.java.ftclient.FTP;

import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.StoreResource;

public class StoreFileWithWrongPathTest extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/wrong-path/image-with-rights" + System.currentTimeMillis() + ".gif";
		InputStream resourceInputStream = getClass().getResourceAsStream("../image-with-rights.gif");
		try {
			StoreResource.storeResource(remoteConnection, remoteResourcePath, resourceInputStream);
//			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC004: The user has no rights to access the remote resource."));
			// TODO: add correct error message: err:FTC003: The remote resource
			// does not exist.
		} finally {
			Disconnect.disconnect(remoteConnection);
		}

	}

}
