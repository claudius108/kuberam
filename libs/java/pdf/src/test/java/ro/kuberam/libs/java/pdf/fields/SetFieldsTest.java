package ro.kuberam.libs.java.pdf.fields;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.junit.Test;

import ro.kuberam.libs.java.pdf.SetFields;

public class SetFieldsTest {

	@Test
	public void test1() throws IOException, XMLStreamException, COSVisitorException {

		InputStream pdfIs = this.getClass().getResourceAsStream("SF.pdf");
		InputStream xfdfIs = this.getClass().getResourceAsStream("sf702-2014-01.xml");

		ByteArrayOutputStream output = SetFields.run(pdfIs, xfdfIs);
		
		try {
			FileOutputStream fos = new FileOutputStream(new File("target/result.pdf"));
			output.writeTo(fos);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			output.close();
		}
	}

}
