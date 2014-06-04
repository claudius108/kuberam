package ro.kuberam.libs.java.ftclient.SFTP;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.DeleteResource;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.StoreResource;

import com.jcraft.jsch.Session;

public class DeleteBinaryResourceTest extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("../sftp-private.key")));
		String remoteResourcePath = sftpTmpDirPath + "/tempFile" + System.currentTimeMillis() + ".txt";

		InputStream resourceInputStream = getClass().getResourceAsStream("../image-with-rights.gif");

		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath,
				resourceInputStream);

		Assert.assertTrue(stored);

		Boolean deleted = DeleteResource.deleteResource(remoteConnection, remoteResourcePath);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(deleted);

	}
}
