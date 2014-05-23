package ro.kuberam.libs.java.ftclient.FTP;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.StoreResource;

import com.jcraft.jsch.Session;

public class StoreTextResourceOnFtpServer extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/home/ftp-user/dir-with-rights/tmp/test" + System.currentTimeMillis()
				+ ".txt";
		InputStream resourceInputStream = getClass().getResourceAsStream("test.txt");
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath,
				resourceInputStream);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(stored);
		}

	}
