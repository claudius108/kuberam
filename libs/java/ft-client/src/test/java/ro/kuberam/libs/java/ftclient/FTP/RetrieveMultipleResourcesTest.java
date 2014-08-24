package ro.kuberam.libs.java.ftclient.FTP;

import java.io.InputStream;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.FTClientAbstractTest;
import ro.kuberam.libs.java.ftclient.ListResources;
import ro.kuberam.libs.java.ftclient.RetrieveResource;

public class RetrieveMultipleResourcesTest extends FTClientAbstractTest {

	@Test
	@Ignore
	public void test() throws Exception {

		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath1 = "/dir-with-rights/";
		StreamResult resources = ListResources.listResources(remoteConnection, remoteResourcePath1);
		String resourcesString = resources.getWriter().toString();

		Assert.assertTrue(resourcesString.contains("image-with-rights.gif"));
		
		String remoteResourcePath2 = "/dir-with-rights/image-with-rights.gif";
		
		InputStream resource1 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		InputStream resource2 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		InputStream resource3 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		InputStream resource4 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		InputStream resource5 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		InputStream resource6 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		InputStream resource7 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		Disconnect.disconnect(remoteConnection);
		// String resource1String = FileUtils.readFileToString(resource1);
		// String resource2String = resource2.getWriter().toString();
		// String resource3String = resource3.getWriter().toString();
		// String resource4String = resource4.getWriter().toString();
		// String resource5String = resource5.getWriter().toString();
		// String resource6String = resource6.getWriter().toString();
		// String resource7String = resource7.getWriter().toString();
		// System.out.println(resource1String);
		// System.out.println(resource2String);
		// System.out.println(resource3String);
		// System.out.println(resource4String);
		// System.out.println(resource5String);
		// System.out.println(resource6String);
		// System.out.println(resource7String);
		// String sampleResourceAsString =
		// "<?xml version=\"1.0\" ?><ft-client:resource xmlns:ft-client=\"http://expath.org/ns/ft-client\" name=\"image-with-rights.gif\" type=\"file\" absolute-path=\"/dir-with-rights/image-with-rights.gif\" last-modified=\"2012-05-14T15:28:00+03:00\" size=\"1010\" human-readable-size=\"1010 bytes\" user=\"1001\" user-group=\"1001\" permissions=\"-rw-rw-rw-\">"
		// + InputStream2ByteArray.convert((InputStream)
		// getClass().getResourceAsStream(
		// "image-with-rights.gif")) + "</ft-client:resource>";
		// Assert.assertTrue(sampleResourceAsString.equals(resource1String));
		// Assert.assertTrue(sampleResourceAsString.equals(resource2String));
		// Assert.assertTrue(sampleResourceAsString.equals(resource3String));
		// Assert.assertTrue(sampleResourceAsString.equals(resource4String));
		// Assert.assertTrue(sampleResourceAsString.equals(resource5String));
		// Assert.assertTrue(sampleResourceAsString.equals(resource6String));
		// Assert.assertTrue(sampleResourceAsString.equals(resource7String));

	}

}
