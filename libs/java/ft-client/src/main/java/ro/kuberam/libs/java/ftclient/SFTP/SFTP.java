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
package ro.kuberam.libs.java.ftclient.SFTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import ro.kuberam.libs.java.ftclient.AbstractConnection;
import ro.kuberam.libs.java.ftclient.ErrorMessages;
import ro.kuberam.libs.java.ftclient.ExpathFTClientModule;
import ro.kuberam.libs.java.ftclient.utils.GenerateResourceElement;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/**
 * Implements a public interface for a SFTP connection.
 * 
 * @author Claudius Teodorescu <claudius.teodorescu@gmail.com>
 */
public class SFTP extends AbstractConnection {

	private static final Logger log = Logger.getLogger(SFTP.class);
	private static String moduleNsUri = "";
	static {
		moduleNsUri = ExpathFTClientModule.NAMESPACE_URI;
	}
	private static String modulePrefix = "";
	static {
		modulePrefix = ExpathFTClientModule.PREFIX;
	}

	public <X> X connect(URI remoteHostURI, String username, String password, String remoteHost,
			int remotePort, String clientPrivateKey) throws Exception {
		long startTime = new Date().getTime();
		X abstractConnection = null;
		remotePort = (remotePort == -1) ? (int) 22 : remotePort;
		// JSch.setLogger(new MyLogger());
		JSch jSch = new JSch();
		Session sftpConnection = null;

		try {
			if (clientPrivateKey.length() != 0) {
				try {
					String uuid = UUID.randomUUID().toString();
					File clientPrivateKeyTempFile = File.createTempFile("SFTPprivateKey" + uuid, ".pem");

					BufferedWriter out = new BufferedWriter(new FileWriter(
							clientPrivateKeyTempFile.getAbsolutePath()));
					out.write(clientPrivateKey);
					out.close();

					// OutputStream out = new
					// FileOutputStream(clientPrivateKeyTempFile);
					// byte buf[] = new byte[1024];
					// int len;
					// while((len = clientPrivateKey.read(buf))>0) {
					// out.write(buf,0,len);
					// }
					// out.close();
					// clientPrivateKey.close();

					jSch.addIdentity(clientPrivateKeyTempFile.getCanonicalPath());
					clientPrivateKeyTempFile.delete();
				} catch (IOException ex) {
					log.error(ex.getMessage(), ex);
				}
			}
			sftpConnection = jSch.getSession(username, remoteHost, remotePort);
			sftpConnection.setConfig("StrictHostKeyChecking", "no");
			sftpConnection.setConfig("PreferredAuthentications", "publickey,password");
			sftpConnection.setTimeout(15000);
			sftpConnection.setPassword(password);
			// System.out.println(SFTPconnection.getServerVersion());
			sftpConnection.connect();
			abstractConnection = (X) sftpConnection;
			log.info("The SFTP sub-module connected to '" + remoteHostURI + "' in "
					+ (new Date().getTime() - startTime) + " ms.");
		} catch (JSchException ex) {
			log.error(ex.getMessage(), ex);
			throw new Exception(ErrorMessages.err_FTC005);
		}

		return abstractConnection;
	}

	public StreamResult listResources(Object abstractConnection, String remoteResourcePath)
			throws Exception {
		long startTime = new Date().getTime();
		log.info("The SFTP sub-module is preparing to retrieve the list of resources for '"
				+ remoteResourcePath + "'.");

		if (!checkIsDirectory(remoteResourcePath)) {
			throw new Exception(ErrorMessages.err_FTC008);
		}

		Session session = (Session) abstractConnection;
		if (!session.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		Channel SFTPchannel = null;
		ChannelSftp connection = null;

		try {
			SFTPchannel = session.openChannel("sftp");
			connection = (ChannelSftp) SFTPchannel;
			SFTPchannel.connect();
		} catch (JSchException ex) {
			log.error(ex.getMessage(), ex);
		}

		List<Object> connectionObject = _checkResourcePath(connection, remoteResourcePath, false);
		connection = (ChannelSftp) connectionObject.get(1);
		Vector<LsEntry> resources = (Vector<LsEntry>) connectionObject.get(2);

		StringWriter writer = new StringWriter();
		XMLStreamWriter xmlWriter = null;

		try {
			xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
			xmlWriter.setPrefix(modulePrefix, moduleNsUri);
			xmlWriter.writeStartDocument();
			xmlWriter.writeStartElement(modulePrefix + ":resources-list");
			xmlWriter.writeNamespace(modulePrefix, moduleNsUri);
			xmlWriter.writeAttribute("absolute-path", remoteResourcePath);
			for (LsEntry resource : resources) {
				String resourceName = resource.getFilename();
				if (resourceName.equals(".") || resourceName.equals("..")) {
					continue;
				}
				_generateResourceElement(xmlWriter, resource, null, remoteResourcePath + resourceName,
						connection);
			}
			xmlWriter.writeEndElement();
			xmlWriter.writeEndDocument();
			xmlWriter.close();
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}

		StreamResult resultAsStreamResult = new StreamResult(writer);

		connection.disconnect();

		log.info("The SFTP sub-module retrieved the list of resources in "
				+ (new Date().getTime() - startTime) + " ms.");

		return resultAsStreamResult;
	}

	public InputStream retrieveResource(Object abstractConnection, String remoteResourcePath)
			throws Exception {
		long startTime = new Date().getTime();
		Session session = (Session) abstractConnection;
		if (!session.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		Channel channel = null;
		ChannelSftp connection = null;
		try {
			channel = session.openChannel("sftp");
			connection = (ChannelSftp) channel;
			channel.connect();
		} catch (JSchException ex) {
			log.error(ex.getMessage(), ex);
		}

		List<Object> connectionObject = _checkResourcePath(connection, remoteResourcePath, true);

		Vector<LsEntry> resources = (Vector<LsEntry>) connectionObject.get(2);

		InputStream is = (InputStream) connectionObject.get(3);

		log.info("The SFTP sub-module retrieved the resource '" + remoteResourcePath + "' in "
				+ (new Date().getTime() - startTime) + " ms.");

		return is;
	}

	public boolean storeResource(Object abstractConnection, String remoteDirectoryPath,
			String resourceName, InputStream resourceInputStream) throws Exception {
		long startTime = new Date().getTime();
		Session session = (Session) abstractConnection;
		if (!session.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		Boolean result = true;
		Channel channel = null;
		ChannelSftp SFTPconnection = null;
		try {
			channel = session.openChannel("sftp");
			SFTPconnection = (ChannelSftp) channel;
			channel.connect();
		} catch (JSchException ex) {
			log.error(ex.getMessage(), ex);
		}
		try {
			List SFTPconnectionObject = null;
			if (resourceName.length() == 0) {
				resourceName = remoteDirectoryPath.substring(remoteDirectoryPath.lastIndexOf("/") + 1);
				remoteDirectoryPath = remoteDirectoryPath
						.substring(0, remoteDirectoryPath.lastIndexOf("/"));
				_checkResourcePath(SFTPconnection, remoteDirectoryPath, false);
				SFTPconnection.mkdir(resourceName);
				log.info("remoteDirectoryPath '" + remoteDirectoryPath + "'");
				log.info("resourceName '" + resourceName + "'");
			} else {
				_checkResourcePath(SFTPconnection, remoteDirectoryPath, false);
				SFTPconnection.put(resourceInputStream, resourceName);
				resourceInputStream.close();
			}
			log.info("The SFTP sub-module stored the resource '" + resourceName + "' at '"
					+ remoteDirectoryPath + "' in " + (new Date().getTime() - startTime) + " ms.");
		} catch (SftpException ex) {
			System.out.println("ex.getMessage(): " + ex.getMessage() + ".\n");
			log.error(ex.getMessage(), ex);
			result = false;
		}

		return result;
	}

	public boolean deleteResource(Object abstractConnection, String remoteResourcePath) throws Exception {
		long startTime = new Date().getTime();
		// JSch.setLogger(new MyLogger());
		Session session = (Session) abstractConnection;
		if (!session.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		Boolean result = true;
		Channel channel = null;
		ChannelSftp SFTPconnection = null;
		try {
			channel = session.openChannel("sftp");
			SFTPconnection = (ChannelSftp) channel;
			channel.connect();
		} catch (JSchException ex) {
			log.error(ex.getMessage(), ex);
		}

		List<Object> SFTPconnectionObject = _checkResourcePath(SFTPconnection, remoteResourcePath, false);
		SFTPconnection = (ChannelSftp) SFTPconnectionObject.get(1);

		try {
			if ((Boolean) SFTPconnectionObject.get(0)) {
				SFTPconnection.rmdir(remoteResourcePath);
			} else {
				SFTPconnection.rm(remoteResourcePath);
			}
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
			// result = false;
		}

		log.info("The SFTP sub-module deleted the resource '" + remoteResourcePath + "' in "
				+ (new Date().getTime() - startTime) + " ms.");

		return result;
	}

	public static Boolean disconnect(Object abstractConnection) throws Exception {
		long startTime = new Date().getTime();
		Session SFTPconnection = (Session) abstractConnection;
		if (!SFTPconnection.isConnected()) {
			throw new Exception(ErrorMessages.err_FTC002);
		}

		Boolean result = true;

		try {
			// close the Connection
			SFTPconnection.disconnect();
			log.info("The SFTP sub-module disconnected in " + (new Date().getTime() - startTime) + " ms.");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			result = false;
		} finally {
			if (SFTPconnection.isConnected()) {
				try {
					SFTPconnection.disconnect();
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					result = false;
				}
			}
		}

		return result;
	}

	public static List<Object> _checkResourcePath(ChannelSftp connection, String remoteResourcePath,
			Boolean getInputStream) throws Exception, SftpException {
		InputStream is = null;
		Vector<LsEntry> resources = null;
		List<Object> connectionObject = new LinkedList<Object>();
		SftpATTRS stat = null;

		try {
			stat = connection.lstat(remoteResourcePath);
		} catch (SftpException ex) {
			throw new Exception(ErrorMessages.err_FTC003);
		}
		try {
			// case when the resource is directory
			if (stat.isDir()) {
				connectionObject.add(true);
				connection.cd(remoteResourcePath);
				resources = connection.ls(".");
			} else {// case when the resource is not directory
				connectionObject.add(false);
				is = connection.get(remoteResourcePath);
				resources = connection.ls(remoteResourcePath);
			}
		} catch (SftpException ex) {
			throw new Exception(ErrorMessages.err_FTC004);
		}

		connectionObject.add(connection);
		connectionObject.add(resources);

		if (getInputStream) {
			connectionObject.add(is);
		} else {
			if (is != null) {
				is.close();
			}
		}

		return connectionObject;
	}

	private static void _generateResourceElement(XMLStreamWriter xmlWriter, LsEntry resource,
			InputStream is, String resourceAbsolutePath, ChannelSftp connection) throws IOException,
			Exception {

		String resourceName = resource.getFilename();
		String resourceType = ((resource.getAttrs().isDir()) ? "directory" : (((resource.getAttrs()
				.isLink()) ? "link" : "file")));
		DateFormat SFTPdateStringFormatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
		Date SFTPdate = null;
		try {
			SFTPdate = (Date) SFTPdateStringFormatter.parse(resource.getAttrs().getMtimeString());
		} catch (ParseException ex) {
			log.error(ex);
		}
		DateFormat XSDdateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		String lastModified = XSDdateTimeFormatter.format(SFTPdate);
		lastModified = lastModified.replace(" ", "T");
		lastModified = lastModified.substring(0, 22) + ":" + lastModified.substring(22, 24);
		String userDescription = resource.getLongname().substring(10).trim().replaceFirst("^[\\S]+", "")
				.trim();
		String user = userDescription.substring(0, userDescription.indexOf(" "));
		String userGroup = userDescription.substring(userDescription.indexOf(" ")).trim();
		userGroup = userGroup.substring(0, userGroup.indexOf(" "));
		long resourceSize = resource.getAttrs().getSize();
		String permissions = resource.getAttrs().getPermissionsString();
		String linkTo = null;
		if (resource.getAttrs().isLink()) {
			linkTo = connection.readlink(resource.getFilename());
		}

		GenerateResourceElement.run(is, xmlWriter, modulePrefix, moduleNsUri, resourceName, resourceType,
				resourceAbsolutePath, lastModified, resourceSize, user, userGroup, permissions, linkTo);
	}

	public static class MyLogger implements com.jcraft.jsch.Logger {
		static java.util.Hashtable<Integer, String> name = new java.util.Hashtable<Integer, String>();
		static {
			name.put(new Integer(DEBUG), "DEBUG: ");
			name.put(new Integer(INFO), "INFO: ");
			name.put(new Integer(WARN), "WARN: ");
			name.put(new Integer(ERROR), "ERROR: ");
			name.put(new Integer(FATAL), "FATAL: ");
		}

		public boolean isEnabled(int level) {
			return true;
		}

		public void log(int level, String message) {
			System.err.print(name.get(new Integer(level)));
			System.err.println(message);
		}
	}
}
