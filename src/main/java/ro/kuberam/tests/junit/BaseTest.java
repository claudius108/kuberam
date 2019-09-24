package ro.kuberam.tests.junit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.After;
import org.junit.Test;

public class BaseTest {

	@Rule
	public TestName name = new TestName();

	@Before
	public void beforeTest() {
		System.out.println("Starting test '" + name.getMethodName() + "'...");
		startTime = new Date().getTime();
	}

	@After
	public void afterTest() {
		System.out.println("Duration of test: "
				+ (new Date().getTime() - startTime) + " ms.\n");
	}

	private long startTime;

	public static File generate5MbTempFile() throws IOException {

		String uuid = UUID.randomUUID().toString();
		File tempFile = File.createTempFile(uuid, ".txt");
		OutputStream outputStream = new FileOutputStream(tempFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0, il = 100000; i < il; i++) {
			baos.write("1111111111111111111111111111111111111111111111111111"
					.getBytes());
		}
		baos.writeTo(outputStream);
		baos.close();

		return tempFile;
	}

	public static String generate5MbTempString() throws IOException {

		String tempString = "";
		for (int i = 0, il = 1000; i < il; i++) {
			tempString = "1111111111111111111111111111111111111111111111111111"
					+ tempString;
		}

		return tempString;
	}

	public static String prettyPrintXmlString(String xmlString)
			throws Exception {
		Source xmlInput = new StreamSource(new StringReader(xmlString));
		StreamResult xmlOutput = new StreamResult(new StringWriter());

		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "5");
		transformer.transform(xmlInput, xmlOutput);

		return xmlOutput.getWriter().toString();
	}

	public static Document parseXmlString(String xmlString) throws IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = null;
		
		InputSource is = new InputSource(new StringReader(xmlString));

		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
		}

		Document doc = null;
		try {
			doc = db.parse(is);
		} catch (SAXException ex) {
			ex.getMessage();
		} catch (IOException ex) {
			ex.getMessage();
		}

		return doc;
	}
}
