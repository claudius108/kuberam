package ro.kuberam.libs.java.pdf.fields;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import ro.kuberam.libs.java.pdf.SetFields;

public class SetFieldsTest {

	@Test
	public void test1() throws IOException, XMLStreamException {

		InputStream xfdfIs = this.getClass().getResourceAsStream("sf702-2014-01.xml");

		SetFields.run(xfdfIs);

	}

}
