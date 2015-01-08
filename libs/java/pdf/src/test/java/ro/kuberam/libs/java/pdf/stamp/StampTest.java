package ro.kuberam.libs.java.pdf.stamp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.pdfbox.exceptions.COSVisitorException;
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
	public void testPdfbox() throws IOException {
		InputStream pdfIs = new FileInputStream(pdfFilePath);

		ByteArrayOutputStream output = null;

		try {
			output = Stamper.run(pdfIs, "Stamped!", "#stamp-1",
					"#stamp-1 {left: 100px; top: 100px; font-family: Arial; font-size: 14px; color: #ff8000;}");
			FileOutputStream fos = new FileOutputStream(new File("target/stamped-document.pdf"));
			output.writeTo(fos);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		} finally {
			output.close();
		}

	}
}
