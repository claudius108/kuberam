/*
 *  Copyright (C) 2011 Claudius Teodorescu
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package ro.kuberam.libs.java.ftclient.FTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import ro.kuberam.libs.java.ftclient.AbstractConnection;
import ro.kuberam.libs.java.ftclient.ErrorMessages;
import ro.kuberam.libs.java.ftclient.ExpathFTClientModule;
import ro.kuberam.libs.java.ftclient.utils.GenerateResourceElement;

/**
 * Implements a public interface for a FTP connection.
 * 
 * @author Claudius Teodorescu <claudius.teodorescu@gmail.com>
 */
public class FTP extends AbstractConnection {
	private static final Logger log = Logger.getLogger(FTP.class);
	private static String moduleNsUri = "";
	static {
		moduleNsUri = ExpathFTClientModule.NAMESPACE_URI;
	}
	private static String modulePrefix = "";
	static {
		modulePrefix = ExpathFTClientModule.PREFIX;
	}

	public <X> X connect(URI remoteHostUri, String username, String password, String remoteHost,
			int remotePort, String options) throws Exception {
		long startTime = new Date().getTime();
		X abstractConnection = null;
		FTPClient ftpConnection = new FTPClient();
		try {
			remotePort = (remotePort == -1) ? (int) 21 : remotePort;
			ftpConnection.setDefaultTimeout(60 * 1000);
			ftpConnection.setRemoteVerificationEnabled(false);
			// FTPconnection.setSoTimeout( 60 * 1000 );
			// FTPconnection.setDataTimeout( 60 * 1000 );
			ftpConnection.connect(remoteHost, remotePort);
			ftpConnection.login(username, password);
			ftpConnection.enterLocalPassiveMode();
			ftpConnection.setFileType(FTPClient.BINARY_FILE_TYPE);
			// FTPconnection.setControlKeepAliveTimeout(300);
			// Check reply code for success
			int reply = ftpConnection.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpConnection.disconnect();
				throw new Exception(ErrorMessages.err_FTC005);
			} else {
				abstractConnection = (X) ftpConnection;
			}
		} catch (IOException se) {
			if (ftpConnection.isConnected()) {
				try {
					ftpConnection.disconnect();
				} catch (IOException ioe) {
					throw new Exception(ErrorMessages.err_FTC005);
				}
			}
		}
		log.info("The FTP sub-module connected to '" + remoteHostUri + "' in "
				+ (new Date().getTime() - startTime) + " ms.");
		return abstractConnection;
	}

	public StreamResult listResources(Object abstractConnection, String remoteResourcePath)
			throws Exception {
		long startTime = new Date().getTime();
		
		boolean isDirectory = checkIsDirectory(remoteResourcePath);

		if (!isDirectory) {
			throw new Exception(ErrorMessages.err_FTC008);
		}

		FTPClient connection = (FTPClient) abstractConnection;
		if (!connection.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		List<Object> connectionObject = _checkResourcePath(connection, remoteResourcePath, "list-resources", isDirectory);

		System.out.println("resources: " + connectionObject.size());

		FTPFile[] resources = (FTPFile[]) connectionObject.get(1);
		StringWriter writer = new StringWriter();
		XMLStreamWriter xmlWriter = null;

		try {
			xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
			xmlWriter.setPrefix(modulePrefix, moduleNsUri);
			xmlWriter.writeStartDocument();
			xmlWriter.writeStartElement(modulePrefix + ":resources-list");
			xmlWriter.writeNamespace(modulePrefix, moduleNsUri);
			xmlWriter.writeAttribute("absolute-path", remoteResourcePath);
			for (FTPFile resource : resources) {
				_generateResourceElement(xmlWriter, resource, null, remoteResourcePath + resource.getName());
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.close();
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}

		// FTPconnection.completePendingCommand();
		StreamResult resultAsStreamResult = new StreamResult(writer);
		log.info("The FTP sub-module retrieved the list of resources in "
				+ (new Date().getTime() - startTime) + " ms.");

		return resultAsStreamResult;
	}

	public StreamResult getResourceMetadata(Object abstractConnection, String remoteResourcePath)
			throws Exception {
		long startTime = new Date().getTime();
		FTPClient FTPconnection = (FTPClient) abstractConnection;
		
		if (!FTPconnection.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		List<Object> FTPconnectionObject = _checkResourcePath(FTPconnection, remoteResourcePath,
				"get-resource-metadata", checkIsDirectory(remoteResourcePath));

		FTPFile[] resources = (FTPFile[]) FTPconnectionObject.get(1);

		StringWriter writer = new StringWriter();
		XMLStreamWriter xmlWriter = null;

		try {
			xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
			xmlWriter.setPrefix(modulePrefix, moduleNsUri);
			xmlWriter.writeStartDocument();
			for (FTPFile resource : resources) {
				_generateResourceElement(xmlWriter, resource, null, remoteResourcePath);
			}
			xmlWriter.writeEndDocument();
			xmlWriter.close();
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}

		// FTPconnection.completePendingCommand();
		StreamResult resultAsStreamResult = new StreamResult(writer);

		log.info("The FTP sub-module retrieved the metadata for resource '" + remoteResourcePath + "' in "
				+ (new Date().getTime() - startTime) + " ms.");

		return resultAsStreamResult;
	}

	public InputStream retrieveResource(Object abstractConnection, String remoteResourcePath)
			throws Exception {
		long startTime = new Date().getTime();
		FTPClient connection = (FTPClient) abstractConnection;
		if (!connection.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		_checkResourcePath(connection, remoteResourcePath, "retrieve-resource", checkIsDirectory(remoteResourcePath));

		InputStream is = connection.retrieveFileStream(remoteResourcePath);

		log.info("The FTP sub-module retrieved the resource '" + remoteResourcePath + "' in "
				+ (new Date().getTime() - startTime) + " ms.");

		return is;
	}

	public boolean storeResource(Object abstractConnection, String remoteDirectoryPath,
			String resourceName, InputStream resourceInputStream) throws Exception {
		long startTime = new Date().getTime();
		FTPClient connection = (FTPClient) abstractConnection;
		if (!connection.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		Boolean result = true;
		try {
			if (resourceName.length() == 0) {
				resourceName = remoteDirectoryPath.substring(remoteDirectoryPath.lastIndexOf("/") + 1);
				remoteDirectoryPath = remoteDirectoryPath
						.substring(0, remoteDirectoryPath.lastIndexOf("/"));
				_checkResourcePath(connection, remoteDirectoryPath, "store-resource", true);
				result = connection.makeDirectory(resourceName);
			} else {
				_checkResourcePath(connection, remoteDirectoryPath, "store-resource", false);
				result = connection.storeFile(resourceName, resourceInputStream);
				resourceInputStream.close();
			}
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			// TODO: add throw exception here for cases when server doesn't
			// allow storage of file - a use case is when vsftpd was configured
			// with mandatory SSL encryption
			result = false;
		}

		// if(!FTPconnection.completePendingCommand()) {
		// throw new Exception(
		// "err:FTC007: The current operation failed.");
		// }

		log.info("The FTP sub-module stored the resource '" + resourceName + "' at '" + remoteDirectoryPath
				+ "' in " + (new Date().getTime() - startTime) + " ms.");

		return result;
	}

	public boolean deleteResource(Object abstractConnection, String remoteResourcePath) throws Exception {
		long startTime = new Date().getTime();
		FTPClient FTPconnection = (FTPClient) abstractConnection;
		if (!FTPconnection.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		Boolean result = true;
		List<Object> FTPconnectionObject = _checkResourcePath(FTPconnection, remoteResourcePath,
				"delete-resource", checkIsDirectory(remoteResourcePath));

		try {
			if ((Boolean) FTPconnectionObject.get(0)) {
				FTPconnection.removeDirectory(remoteResourcePath);
				log.info("The FTP sub-module deleted the directory in "
						+ (new Date().getTime() - startTime) + " ms.");
			} else {
				FTPconnection.deleteFile(remoteResourcePath);
				log.info("The FTP sub-module deleted the file in " + (new Date().getTime() - startTime)
						+ " ms.");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			result = false;
		}

		// if (!FTPconnection.completePendingCommand()) {
		// throw new Exception("err:FTC007: The current operation failed.");
		// }

		log.info("The FTP sub-module deleted the resource '" + remoteResourcePath + "' in "
				+ (new Date().getTime() - startTime) + " ms.");

		return result;
	}

	public static Boolean disconnect(Object abstractConnection) throws Exception {
		long startTime = new Date().getTime();
		FTPClient FTPconnection = (FTPClient) abstractConnection;
		if (!FTPconnection.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		Boolean result = true;

		// try {
		// // close the Connection
		// long startTime = new Date().getTime();
		// //FTPconnection.logout();
		// log.info("Logout was done in " + (new Date().getTime() - startTime) +
		// " ms.");
		// } catch (IOException ioe) {
		// // log.error(ioe.getMessage(), ioe);
		// result = false;
		// } finally {
		// long startTime = new Date().getTime();
		// try {
		// FTPconnection.disconnect();
		// } catch (IOException ioe) {
		// log.error(ioe.getMessage(), ioe);
		// result = false;
		// }
		// log.info("Disconnection was done in " + (new Date().getTime() -
		// startTime) + " ms.");
		// }

		try {
			// FTPconnection.logout();
			FTPconnection.disconnect();
			log.info("The FTP sub-module disconnected in " + (new Date().getTime() - startTime) + " ms.");
		} catch (IOException ioe) {
			log.error(ioe.getMessage(), ioe);
			result = false;
		}

		return result;
	}

	private static List<Object> _checkResourcePath(FTPClient connection, String remoteResourcePath,
			String actionName, boolean isDirectory) throws Exception {
		FTPFile[] resources = null;
		List<Object> connectionObject = new LinkedList<Object>();

		boolean remoteDirectoryExists = connection.changeWorkingDirectory(remoteResourcePath); 
				
		if (isDirectory) {
			int returnCode = connection.getReplyCode();

			// check if the remote directory exists
			if (returnCode == 550) {
				throw new Exception(ErrorMessages.err_FTC003);
			}			
		}	

		connectionObject.add(remoteDirectoryExists);

		// check if the user has rights as to the resource
		resources = connection.listFiles(remoteResourcePath);

//		if (!actionName.equals("list-resources") && !remoteDirectoryExists) {
//			// System.out.println("permissions; "
//			// + resources[0].hasPermission(FTPFile.USER_ACCESS,
//			// FTPFile.READ_PERMISSION));
//		}

		// if (!remoteDirectoryExists) {
		// FTPconnection.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		// is = FTPconnection.retrieveFileStream(remoteResourcePath);
		// if (is == null || resources.length == 0) {
		// throw new
		// Exception(ErrorMessages.err_FTC004);
		// }
		// }

		connectionObject.add(resources);

		return connectionObject;
	}

	private static void _generateResourceElement(XMLStreamWriter xmlWriter, FTPFile resource,
			InputStream is, String resourceAbsolutePath) throws IOException, Exception {

		String resourceName = resource.getName();
		String resourceType = ((resource.getType() == 1) ? "directory"
				: (((resource.getType() == 0) ? "file" : "link")));
		Calendar resourceTimeStamp = resource.getTimestamp();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		String lastModified = formatter.format(resourceTimeStamp.getTimeInMillis());
		lastModified = lastModified.replace(" ", "T");
		lastModified = lastModified.substring(0, 22) + ":" + lastModified.substring(22, 24);
		long resourceSize = resource.getSize();
		String user = resource.getUser();
		String userGroup = resource.getGroup();
		String permissions = resource.getRawListing().substring(0, 10);
		String linkTo = resource.getLink();

		GenerateResourceElement.run(is, xmlWriter, modulePrefix, moduleNsUri, resourceName, resourceType,
				resourceAbsolutePath, lastModified, resourceSize, user, userGroup, permissions, linkTo);
	}
}
