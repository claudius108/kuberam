package ro.kuberam.libs.java.ftclient;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import ro.kuberam.libs.java.ftclient.Connect;
import ro.kuberam.libs.java.ftclient.DeleteResource;
import ro.kuberam.libs.java.ftclient.Disconnect;
import ro.kuberam.libs.java.ftclient.GetResourceMetadata;
import ro.kuberam.libs.java.ftclient.ListResources;
import ro.kuberam.libs.java.ftclient.RetrieveResource;
import ro.kuberam.libs.java.ftclient.StoreResource;
import ro.kuberam.libs.java.ftclient.FTP.FTP;
import ro.kuberam.libs.java.ftclient.utils.Base64;
import ro.kuberam.libs.java.ftclient.utils.InputStream2ByteArray;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import ro.kuberam.tests.junit.BaseTest;

import com.jcraft.jsch.Session;

public class FTClientAbstractTest extends BaseTest {

	public static Properties connectionProperties = new Properties();
	public static String ftpHomeDirPath = "/dir-with-rights";
	public static String ftpTmpDirPath = ftpHomeDirPath + "/tmp";
	public static String sftpHomeDirPath = "/home/ftp-user" + ftpHomeDirPath;
	public static String sftpTmpDirPath = sftpHomeDirPath + "/tmp";	
	static {
		try {
			connectionProperties.load(FTClientAbstractTest.class
					.getResourceAsStream("connection.properties"));
			// clean the ftp temp directory
			FileUtils.cleanDirectory(new File(sftpHomeDirPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static FTPClient initializeFtpConnection(String URIstring) throws URISyntaxException, Exception {
		FTPClient remoteConnection = Connect.connect(new URI(URIstring), "");
		return remoteConnection;
	}

	public static Session initializeSftpConnection(String URIstring, String clientPrivateKey)
			throws URISyntaxException, Exception {
		Session remoteConnection = Connect.connect(new URI(URIstring), clientPrivateKey);
		return remoteConnection;
	}

	public static String getTextContent(StreamResult resource) throws Exception {

		String resourceAsString = resource.getWriter().toString();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(resourceAsString.getBytes("UTF-8")));

		return doc.getDocumentElement().getFirstChild().getTextContent();
	}

	public static String serializeToString(StreamResult resource) throws Exception {
		return resource.getWriter().toString();
	}

	public static String getBinaryResourceAsString(String resourcePath) throws Exception {
		System.out.println("resourcePath: " + resourcePath.substring(3));
		System.out.println("resourcePath: "
				+ FTClientAbstractTest.class.getResource(resourcePath.substring(3)));
		return new String(InputStream2ByteArray.convert((InputStream) FTClientAbstractTest.class
				.getResourceAsStream(resourcePath.substring(3))));
	}

	public static String getBinaryResourceAsBase64String(String resourcePath) throws Exception {
		return Base64.encodeToString(
				InputStream2ByteArray.convert((InputStream) FTClientAbstractTest.class
						.getResourceAsStream(resourcePath.substring(3))), true).replace("\r", "");
	}

	@Test
	public void test03() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = ftpTmpDirPath + "/image-with-rights" + System.currentTimeMillis()
				+ ".gif";
		InputStream resourceInputStream = getClass().getResourceAsStream("image-with-rights.gif");
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath,
				resourceInputStream);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(stored);
	}

	@Test
	public void test06() throws URISyntaxException, Exception {
		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/home/ftp-user/dir-with-rights/tmp/image-with-rights"
				+ System.currentTimeMillis() + ".gif";
		InputStream resourceInputStream = getClass().getResourceAsStream("image-with-rights.gif");
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath,
				resourceInputStream);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(stored);
	}

	@Test
	public void test07() throws URISyntaxException, Exception {
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

	@Test
	public void test10() throws URISyntaxException, Exception {
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

	@Ignore
	@Test
	public void test12() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/image-no-rights.gif";
		try {
			RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC004: The user has no rights to access the remote resource."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Ignore
	@Test
	public void test13() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/non-existing-directory/";
		try {
			ListResources.listResources(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(
					e.getLocalizedMessage(),
					e.getLocalizedMessage().equals(
							"err:FTC004: The user has no rights to access the remote resource."));
			// TODO: add correct error message: err:FTC003: The remote resource
			// does not exist.
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Test
	public void test14() throws URISyntaxException, Exception {
		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/non-existing-directory";
		try {
			ListResources.listResources(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage(),
					e.getLocalizedMessage().equals("err:FTC003: The remote resource does not exist."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Ignore
	@Test
	public void test15() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/dir-without-rights";
		try {
			ListResources.listResources(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC004: The user has no rights to access the remote resource."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Test
	public void test16() throws URISyntaxException, Exception {
		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/home/ftp-user/dir-with-rights/dir-without-rights";
		try {
			ListResources.listResources(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC004: The user has no rights to access the remote resource."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Ignore
	@Test
	public void test17() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/non-existing-image.gif";
		try {
			RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC004: The user has no rights to access the remote resource."));
			// TODO: add correct error message: err:FTC003: The remote resource
			// does not exist.
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Test
	public void test18() throws URISyntaxException, Exception {
		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/home/ftp-user/dir-with-rights/non-existing-image.gif";
		try {
			RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC003: The remote resource does not exist."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Test
	public void test19() throws URISyntaxException, Exception {
		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/home/ftp-user/dir-with-rights/image-no-rights.gif";
		try {
			RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC004: The user has no rights to access the remote resource."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Ignore
	@Test
	public void storeFileWrongPathWithFtp() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/wrong-path/image-with-rights" + System.currentTimeMillis() + ".gif";
		InputStream resourceInputStream = getClass().getResourceAsStream("image-with-rights.gif");
		try {
			StoreResource.storeResource(remoteConnection, remoteResourcePath, resourceInputStream);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC004: The user has no rights to access the remote resource."));
			// TODO: add correct error message: err:FTC003: The remote resource
			// does not exist.
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Ignore
	@Test
	public void storeFileWithoutRightsWithFtp() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/dir-without-rights/image-with-rights"
				+ System.currentTimeMillis() + ".gif";
		InputStream resourceInputStream = getClass().getResourceAsStream("image-with-rights.gif");
		try {
			StoreResource.storeResource(remoteConnection, remoteResourcePath, resourceInputStream);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e.getLocalizedMessage().equals(
					"err:FTC004: The user has no rights to access the remote resource."));
		} finally {
			Disconnect.disconnect(remoteConnection);
		}
	}

	@Test
	public void test22() throws URISyntaxException, Exception {
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

	@Test
	public void deleteFolderWithFtp() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/tmp/tempFolder" + System.currentTimeMillis() + "/";
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath, null);
		Assert.assertTrue(stored);
		Boolean deleted = DeleteResource.deleteResource(remoteConnection, remoteResourcePath);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(deleted);
	}

	@Test
	public void deleteFileWithFtp() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/tmp/test" + System.currentTimeMillis() + ".txt";
		(new File("/home/ftp-user/" + remoteResourcePath)).createNewFile();
		Boolean deleted = DeleteResource.deleteResource(remoteConnection, remoteResourcePath);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(deleted);
	}

	@Test
	public void createDirWithFtp() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/tmp/tempFolder" + System.currentTimeMillis() + "/";
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath, null);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(stored);
	}

	@Test
	public void createDirectoryWithSftp() throws URISyntaxException, Exception {
		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/home/ftp-user/dir-with-rights/tmp/tempFolder"
				+ System.currentTimeMillis() + "/";
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath, null);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(stored);
	}

	@Test
	public void test26() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/tmp/tempFolder" + System.currentTimeMillis() + "/";
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath, null);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(stored);
	}

	@Test
	public void test27() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath = "/dir-with-rights/tmp/tempFolder" + System.currentTimeMillis() + "/";
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath, null);
		Assert.assertTrue(stored);
	}

	@Test
	public void getResourceMetadataFromFtpServer() throws URISyntaxException, Exception {
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

	@Test
	@Ignore
	public void test29() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		String remoteResourcePath1 = "/dir-with-rights";
		StreamResult resources = ListResources.listResources(remoteConnection, remoteResourcePath1);
		String resourcesString = resources.getWriter().toString();
		System.out.println(resourcesString);
		Assert.assertTrue(resourcesString.contains("image-with-rights.gif"));
		String remoteResourcePath2 = "/dir-with-rights/image-with-rights.gif";
		StreamResult resource1 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		StreamResult resource2 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		StreamResult resource3 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		StreamResult resource4 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		StreamResult resource5 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		StreamResult resource6 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		StreamResult resource7 = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath2);
		Disconnect.disconnect(remoteConnection);
		String resource1String = resource1.getWriter().toString();
		String resource2String = resource2.getWriter().toString();
		String resource3String = resource3.getWriter().toString();
		String resource4String = resource4.getWriter().toString();
		String resource5String = resource5.getWriter().toString();
		String resource6String = resource6.getWriter().toString();
		String resource7String = resource7.getWriter().toString();
		System.out.println(resource1String);
		System.out.println(resource2String);
		System.out.println(resource3String);
		System.out.println(resource4String);
		System.out.println(resource5String);
		System.out.println(resource6String);
		System.out.println(resource7String);
		String sampleResourceAsString = "<?xml version=\"1.0\" ?><ft-client:resource xmlns:ft-client=\"http://expath.org/ns/ft-client\" name=\"image-with-rights.gif\" type=\"file\" absolute-path=\"/dir-with-rights/image-with-rights.gif\" last-modified=\"2012-05-14T15:28:00+03:00\" size=\"1010\" human-readable-size=\"1010 bytes\" user=\"1001\" user-group=\"1001\" permissions=\"-rw-rw-rw-\">"
				+ InputStream2ByteArray.convert((InputStream) getClass().getResourceAsStream(
						"image-with-rights.gif")) + "</ft-client:resource>";
		Assert.assertTrue(sampleResourceAsString.equals(resource1String));
		Assert.assertTrue(sampleResourceAsString.equals(resource2String));
		Assert.assertTrue(sampleResourceAsString.equals(resource3String));
		Assert.assertTrue(sampleResourceAsString.equals(resource4String));
		Assert.assertTrue(sampleResourceAsString.equals(resource5String));
		Assert.assertTrue(sampleResourceAsString.equals(resource6String));
		Assert.assertTrue(sampleResourceAsString.equals(resource7String));
	}

	@Test
	@Ignore
	public void retrieveLargeResource() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection("ftp://ftp.mozilla.org");
		String remoteResourcePath = "/pub/firefox/releases/9.0b6/linux-i686/en-US/firefox-9.0b6.tar.bz2";
		StreamResult resource = RetrieveResource.retrieveResource(remoteConnection, remoteResourcePath);
		Disconnect.disconnect(remoteConnection);
		String resourceString = resource.getWriter().toString();
	}

	@Test
	public void deleteDirectoryWithSftp() throws URISyntaxException, Exception {
		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/home/ftp-user/dir-with-rights/tmp/tempFolder"
				+ System.currentTimeMillis() + "/";
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath, null);
		Assert.assertTrue(stored);
		System.out.println("Stored resource: " + remoteResourcePath + ".\n");
		Boolean deleted = DeleteResource.deleteResource(remoteConnection, remoteResourcePath);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(deleted);
	}

	@Test
	public void deleteFileWithSftp() throws URISyntaxException, Exception {
		Session remoteConnection = initializeSftpConnection(
				connectionProperties.getProperty("sftp-server-connection-url"),
				IOUtils.toString(getClass().getResourceAsStream("Open-Private-Key")));
		String remoteResourcePath = "/home/ftp-user/dir-with-rights/tmp/tempFile"
				+ System.currentTimeMillis() + ".txt";
		InputStream resourceInputStream = getClass().getResourceAsStream("image-with-rights.gif");
		Boolean stored = StoreResource.storeResource(remoteConnection, remoteResourcePath,
				resourceInputStream);
		Assert.assertTrue(stored);
		System.out.println("Stored resource: " + remoteResourcePath + ".\n");
		Boolean deleted = DeleteResource.deleteResource(remoteConnection, remoteResourcePath);
		Disconnect.disconnect(remoteConnection);
		Assert.assertTrue(deleted);
	}

	@Test
	public void _checkDirectoryWithRightsTest() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		try {
			_checkResourcePath(remoteConnection, "/dir-with-rights/");
			// Assert.assertTrue(false);
			FTP.disconnect(remoteConnection);
		} catch (Exception e) {
			// Assert.assertTrue(e.getLocalizedMessage().equals(
			// "err:FTC003: The remote resource does not exist."));
			FTP.disconnect(remoteConnection);
		}
	}

	@Test
	public void _checkDirectoryWithoutRightsTest() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		try {
			_checkResourcePath(remoteConnection, "/dir-with-rights/dir-without-rights/");
			// Assert.assertTrue(false);
			FTP.disconnect(remoteConnection);
		} catch (Exception e) {
			// Assert.assertTrue(e.getLocalizedMessage().equals(
			// "err:FTC003: The remote resource does not exist."));
			FTP.disconnect(remoteConnection);
		}
	}

	@Test
	public void _checkDirectoryNonExistingTest() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		try {
			_checkResourcePath(remoteConnection, "/non-existing-dir/");
			// Assert.assertTrue(false);
			FTP.disconnect(remoteConnection);
		} catch (Exception e) {
			// Assert.assertTrue(e.getLocalizedMessage().equals(
			// "err:FTC003: The remote resource does not exist."));
			FTP.disconnect(remoteConnection);
		}
	}

	@Test
	public void _checkFileWithRightsTest() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		try {
			_checkResourcePath(remoteConnection, "/dir-with-rights/image-with-rights.gif");
			// Assert.assertTrue(false);
			FTP.disconnect(remoteConnection);
		} catch (Exception e) {
			// Assert.assertTrue(e.getLocalizedMessage().equals(
			// "err:FTC003: The remote resource does not exist."));
			FTP.disconnect(remoteConnection);
		}
	}

	@Test
	public void _checkFileWithoutRightsTest() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		try {
			_checkResourcePath(remoteConnection, "/dir-with-rights/image-no-rights.gif");
			// Assert.assertTrue(false);
			FTP.disconnect(remoteConnection);
		} catch (Exception e) {
			// Assert.assertTrue(e.getLocalizedMessage().equals(
			// "err:FTC003: The remote resource does not exist."));
			FTP.disconnect(remoteConnection);
		}
	}

	@Test
	public void _checkFileNonExistingTest() throws URISyntaxException, Exception {
		FTPClient remoteConnection = initializeFtpConnection(connectionProperties
				.getProperty("ftp-server-connection-url"));
		try {
			_checkResourcePath(remoteConnection, "/dir-with-rights/non-existing-image.gif");
			// Assert.assertTrue(false);
			FTP.disconnect(remoteConnection);
		} catch (Exception e) {
			// Assert.assertTrue(e.getLocalizedMessage().equals(
			// "err:FTC003: The remote resource does not exist."));
			FTP.disconnect(remoteConnection);
		}
	}

	public static List _checkResourcePath(FTPClient FTPconnection, String remoteResourcePath)
			throws IOException, Exception {
		List FTPconnectionObject = new LinkedList();
		boolean resourceIsDirectory = remoteResourcePath.endsWith("/");
		if (resourceIsDirectory) {
			System.out.println("FTPconnection.listFiles(remoteResourcePath) == null: "
					+ Boolean.toString(FTPconnection.listFiles(remoteResourcePath) == null));
			boolean remoteDirectoryExists = FTPconnection.changeWorkingDirectory(remoteResourcePath);
			FTPconnectionObject.add(remoteDirectoryExists);
			if (!remoteDirectoryExists) {
				System.out.println("\n====================" + remoteResourcePath + "====================");
				// System.out.println("FTPconnection.getReplyString(): "
				// + FTPconnection.getReplyString());
				// System.out
				// .println("FTPconnection.getStatus(remoteResourcePath): "
				// + FTPconnection.getStatus(remoteResourcePath));
				System.out.println("FTPconnection.listFiles(remoteResourcePath): "
						+ FTPconnection.listFiles("/").length);

				if (FTPconnection.getStatus(remoteResourcePath) == null) {
					System.out.println("err:FTC004: The user has no rights to access the remote resource.");
					// throw new Exception(
					// "err:FTC004: The user has no rights to access the remote resource.");
				}
				// throw new Exception(
				// "err:FTC003: The remote resource does not exist.");
				System.out.println("err:FTC003: The remote resource does not exist.");
				// throw new Exception(
				// "err:FTC003: The remote resource does not exist.");
			}

		} else {
			if (FTPconnection.listNames(remoteResourcePath).length == 0) {
				System.out.println("err:FTC003: The remote resource does not exist.");
				// throw new Exception(
				// "err:FTC003: The remote resource does not exist.");
			} else {
				InputStream is = FTPconnection.retrieveFileStream(remoteResourcePath);
				if (is == null) {
					System.out.println("err:FTC004: The user has no rights to access the remote resource.");
					// throw new Exception(
					// "err:FTC004: The user has no rights to access the remote resource.");
				}
				FTPconnectionObject.add(is);
			}
		}

		return FTPconnectionObject;
	}

	public static void main(String[] args) throws Exception {

	}
}
