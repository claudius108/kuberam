package ro.kuberam.libs.java.ftclient.FTP;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.GetResourceMetadata;

public class GetResourceMetadataFromFtpServer extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/image-with-rights.gif";
		StreamResult resourceMetadata = GetResourceMetadata.getResourceMetadata(remoteConnection,
				remoteResourcePath);
		Disconnect.disconnect(remoteConnection);
		String resourceMetadataString = resourceMetadata.getWriter().toString();
		String sampleResourceMetadataAsString = "<?xml version=\"1.0\" ?><ft-client:resource xmlns:ft-client=\"http://expath.org/ns/ft-client\" name=\"image-with-rights.gif\" type=\"file\" absolute-path=\"/dir-with-rights/image-with-rights.gif\" last-modified=\"2014-01-03T00:04:00+02:00\" size=\"1010\" human-readable-size=\"1010 bytes\" user=\"1001\" user-group=\"1001\" permissions=\"-r--r--rw-\" checksum=\"0\"></ft-client:resource>";
		Assert.assertTrue(resourceMetadataString.equals(sampleResourceMetadataAsString));

	}

}
