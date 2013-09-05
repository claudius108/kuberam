package ro.kuberam.maven.plugins.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class KuberamMojoUtils {

	public static void createOutputDir(File outputDir) {
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
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
		String specFileBaseName = file.getName();
		return (specFileBaseName.contains(".")) ? specFileBaseName.substring(0, specFileBaseName.lastIndexOf(".")) : specFileBaseName;
	}

}
