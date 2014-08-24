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

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

import ro.kuberam.libs.java.ftclient.FTP.FTP;
import ro.kuberam.libs.java.ftclient.utils.Base64;
import ro.kuberam.libs.java.ftclient.utils.InputStream2ByteArray;
import ro.kuberam.tests.junit.BaseTest;

import com.jcraft.jsch.Session;

public class FTClientAbstractTest extends BaseTest {

	public static Properties connectionProperties = new Properties();
	public static String ftpHomeDirPath = File.separator + "dir-with-rights";
	public static String ftpTmpDirPath = ftpHomeDirPath + File.separator + "tmp";
	static {
		try {
			connectionProperties.load(FTClientAbstractTest.class
					.getResourceAsStream("connection.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String sftpHomeDirPath = connectionProperties.getProperty("server-home-folder")
			+ ftpHomeDirPath;
	public static String sftpTmpDirPath = sftpHomeDirPath + File.separator + "tmp";

	// @After
	// public void cleanup() {
	// try {
	// // clean the ftp temp directory
	// FileUtils.cleanDirectory(new File(sftpTmpDirPath));
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

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
		return new String(InputStream2ByteArray.convert((InputStream) FTClientAbstractTest.class
				.getResourceAsStream(resourcePath.substring(3))));
	}

	public static String getBinaryResourceAsBase64String(String resourcePath) throws Exception {
		return Base64.encodeToString(
				InputStream2ByteArray.convert((InputStream) FTClientAbstractTest.class
						.getResourceAsStream(resourcePath.substring(3))), true).replace("\r", "");
	}

	public static String getBinaryResourceAsBase64String(InputStream resource) throws Exception {
		return Base64.encodeToString(InputStream2ByteArray.convert(resource), true).replace("\r", "");
	}

	@Ignore
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

	@Ignore
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

	@Ignore
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

	@Ignore
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

	@Ignore
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

	@Ignore
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

}
