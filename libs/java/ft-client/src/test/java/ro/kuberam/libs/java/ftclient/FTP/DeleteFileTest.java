package ro.kuberam.libs.java.ftclient.FTP;

import java.io.File;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.DeleteResource;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;

public class DeleteFileTest extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/tmp/test" + System.currentTimeMillis() + ".txt";
		(new File("/home/ftp-user/" + remoteResourcePath)).createNewFile();
		Boolean deleted = DeleteResource.deleteResource(remoteConnection, remoteResourcePath);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(deleted);

	}
}
