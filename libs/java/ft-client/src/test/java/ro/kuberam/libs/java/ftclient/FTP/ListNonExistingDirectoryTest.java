package ro.kuberam.libs.java.ftclient.FTP;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.ListResources;

public class ListNonExistingDirectoryTest extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/non-existing-directory/";
		try {
			ListResources.listResources(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			System.out.println("e.getLocalizedMessage(): " + e.getLocalizedMessage());
			Assert.assertTrue(e.getLocalizedMessage(),
					e.getLocalizedMessage().equals("err:FTC003: The remote resource does not exist."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}

	}
}
