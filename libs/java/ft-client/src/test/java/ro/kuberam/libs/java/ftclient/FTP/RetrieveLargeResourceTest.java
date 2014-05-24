package ro.kuberam.libs.java.ftclient.FTP;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;

public class RetrieveLargeResourceTest extends FTClientAbstractTest {

	@Test
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection("ftp://ftp.mozilla.org");
		String remoteResourcePath = "/pub/firefox/releases/9.0b6/linux-i686/en-US/firefox-9.0b6.tar.bz2";
//		StreamResult resource = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath);
//		Disconnect.disconnect(remoteConnection);
//		String resourceString = resource.getWriter().toString();
		
	}

}
