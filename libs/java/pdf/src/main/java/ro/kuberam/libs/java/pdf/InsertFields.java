package ro.kuberam.libs.java.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class InsertFields {

	private static XMLStreamReader parser;

	public static String run(InputStream xfdfIs) throws UnsupportedEncodingException, XMLStreamException {
		ByteArrayOutputStream parserOutput = new ByteArrayOutputStream();
		Writer w = new OutputStreamWriter(parserOutput, "UTF-8");

		XMLInputFactory factory = XMLInputFactory.newInstance();
		parser = factory.createXMLStreamReader(xfdfIs);

		int event = parser.next();

		while ((event = parser.next()) != XMLStreamConstants.END_DOCUMENT) {
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				break;
			}
		}

		return null;
	}

}
