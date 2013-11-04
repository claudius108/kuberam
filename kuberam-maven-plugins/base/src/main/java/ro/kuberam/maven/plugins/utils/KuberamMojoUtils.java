package ro.kuberam.maven.plugins.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;

public class KuberamMojoUtils {

	private static int BUFFER = 2048;

	public static File createDir(File createdDir) {
		if (!createdDir.exists()) {
			createdDir.mkdir();
		}

		return createdDir;
	}

	public static boolean unpack(URL url, File file) {
		if (file.exists()) {
			return false;
		}

		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileOutputStream writer = new FileOutputStream(file);
			url.openConnection();
			InputStream reader = url.openStream();
			byte[] buffer = new byte[153600];
			int bytesRead = 0;
			while ((bytesRead = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, bytesRead);
				buffer = new byte[153600];
			}
			writer.close();
			reader.close();
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Exception occured during unpacking of file '" + file.getName() + "'", e);
		}
	}

	public static String getFileBaseName(File file) {
		String fileName = file.getName();
		return (fileName.contains(".")) ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
	}

	public static String getFileName(String fileName) {
		int lastSeparator = fileName.lastIndexOf(File.separatorChar);
		return lastSeparator == -1 ? null : fileName.substring(0, lastSeparator);
	}

	public static File downloadFromUrl(String urlString, File outputFile) {

		HttpURLConnection connection;
		URL url = null;

		try {
			url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("content-type", "binary/data");
			InputStream in = connection.getInputStream();
			FileOutputStream out = new FileOutputStream(outputFile);

			byte[] b = new byte[BUFFER];
			int count;

			while ((count = in.read(b)) > 0) {
				out.write(b, 0, count);
			}
			out.close();
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return outputFile;
	}

	public static void unzipArchive(File archiveFile, File outputDirectory) {
		try {
			ZipFile ip2countryZipFile = new ZipFile(archiveFile);

			ZipEntry ip2countryEntry = ip2countryZipFile.getEntry("ip2country.db");
			BufferedInputStream is = new BufferedInputStream(ip2countryZipFile.getInputStream(ip2countryEntry));
			int currentByte;
			byte data[] = new byte[BUFFER];

			File ip2countryDb = new File(outputDirectory + File.separator + "ip2country.db");
			FileOutputStream fos = new FileOutputStream(ip2countryDb);
			BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

			while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, currentByte);
			}
			dest.flush();
			dest.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void extract(File zipfile, File outputDir) {
		ZipUnArchiver unArchiver = new ZipUnArchiver();
		unArchiver.setSourceFile(zipfile);
		unArchiver.enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG, "consoleLogger"));
		unArchiver.extract("", outputDir);

	}

}
