package ro.kuberam.libs.java.ftclient.FTP;

import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.StoreResource;

public class StoreBinaryResourceTest extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = ftpTmpDirPath + "/image-with-rights" + System.currentTimeMillis()
				+ ".gif";
		InputStream resourceInputStream = getClass().getResourceAsStream("../image-with-rights.gif");
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath,
				resourceInputStream);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(stored);

	}
}
