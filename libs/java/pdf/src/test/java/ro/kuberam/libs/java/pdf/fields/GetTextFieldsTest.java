package ro.kuberam.libs.java.pdf.fields;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import ro.kuberam.libs.java.pdf.formControls.GetTextFields;

public class GetTextFieldsTest {

	@Test
	public void test1() throws IOException, XMLStreamException {

		InputStream pdfIs = this.getClass().getResourceAsStream("SF.pdf");

		ByteArrayOutputStream output = GetTextFields.run(pdfIs);

		try {
			FileOutputStream fos = new FileOutputStream(new File("target/result.xml"));
			output.writeTo(fos);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			output.close();
		}
	}

}
