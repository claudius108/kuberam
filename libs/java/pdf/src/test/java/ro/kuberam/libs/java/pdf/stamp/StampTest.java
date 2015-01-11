package ro.kuberam.libs.java.pdf.stamp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.junit.Test;

public class StampTest {

	private static File pdfFilePath;
	static {
		try {
			pdfFilePath = new File(StampTest.class.getResource("../formControls/SF.pdf").toURI());

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testStampSignature2() throws IOException {
		InputStream pdfIs = new FileInputStream(pdfFilePath);

		ByteArrayOutputStream output = null;

		StringStamper app = null;
		app = new StringStamper(pdfIs, "Stamped!",
				"left: 70pt; top: 70pt; font-family: Helvetica; font-size: 22pt; color: rgb(144,144,0);");

		try {
			output = app.stamp();
			FileOutputStream fos = new FileOutputStream(new File("target/stamped-document.pdf"));
			output.writeTo(fos);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			output.close();
		}

	}
}
