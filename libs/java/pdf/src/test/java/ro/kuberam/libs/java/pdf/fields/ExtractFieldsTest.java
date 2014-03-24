package ro.kuberam.libs.java.pdf.fields;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import ro.kuberam.libs.java.pdf.ExtractFields;

public class ExtractFieldsTest {

	@Test
	public void testExtractFields() throws IOException, XMLStreamException {

		InputStream pdfIs = this.getClass().getResourceAsStream("SF.pdf");

		System.out.println(ExtractFields.run(pdfIs));

	}


}
 