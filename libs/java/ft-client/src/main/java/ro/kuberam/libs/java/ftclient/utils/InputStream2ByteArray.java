package ro.kuberam.libs.java.ftclient.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStream2ByteArray {

	public static byte[] convert(InputStream is) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try {
			for (int readNum; (readNum = is.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
		} catch (IOException ex) {
			throw new IOException("Could not completely read the input stream.");
		} finally {
			is.close();
		}

		bos.flush();

		return bos.toByteArray();
	}
}
