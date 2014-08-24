package ro.kuberam.libs.java.ftclient.FTP;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.StoreResource;

public class CreateDirectoryTest extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));		
		String remoteResourcePath = "/dir-with-rights/tmp/tempFolder" + System.currentTimeMillis() + "/";		
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath, null);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(stored);

	}
}
