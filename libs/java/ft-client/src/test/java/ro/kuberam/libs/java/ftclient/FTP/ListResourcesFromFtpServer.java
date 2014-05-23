package ro.kuberam.libs.java.ftclient.FTP;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.ListResources;

public class ListResourcesFromFtpServer extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		Disconnect.disconnect(remoteConnection);
		String remoteResourcePath = "/";
		try {
			ListResources.listResources(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC002: The connection was closed by server."));
		}

	}

}
