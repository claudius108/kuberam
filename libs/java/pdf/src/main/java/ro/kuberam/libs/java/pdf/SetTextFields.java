package ro.kuberam.libs.java.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

public class SetTextFields {

	private static XMLStreamReader parser;
	private static ArrayList<String> fieldFullyQualifiedNameList = new ArrayList<String>();
	private static ArrayList<String> fieldValueList = new ArrayList<String>();

	public static ByteArrayOutputStream run(InputStream pdfIs, InputStream xfdfIs)
			throws XMLStreamException, IOException, COSVisitorException {

		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		XMLInputFactory factory = XMLInputFactory.newInstance();
		parser = factory.createXMLStreamReader(xfdfIs);

		int event = parser.next();

		while ((event = parser.next()) != XMLStreamConstants.END_DOCUMENT) {
			String fieldFullyQualifiedName = "";
			String fieldValue = "";

			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				String localName = parser.getLocalName();
				if (localName.equals("field")) {
					fieldFullyQualifiedName = parser.getAttributeValue(0);
					parser.next();
				}
				if (localName.equals("value")) {
					fieldValue = parser.getElementText();
					parser.next();
				}
				break;
			}
			fieldFullyQualifiedNameList.add(fieldFullyQualifiedName);
			fieldValueList.add(fieldValue);
		}

		parser.close();

		List<PDField> fields = acroForm.getFields();
		Iterator<PDField> fieldsIterator = fields.iterator();

		while (fieldsIterator.hasNext()) {
			PDField field = fieldsIterator.next();
			setField(field, field.getPartialName());
		}

		pdfDocument.save(output);
		pdfDocument.close();

		return output;
	}

	private static void setField(PDField field, String sParent) throws IOException {
		List<COSObjectable> kids = field.getKids();
		if (kids != null) {
			Iterator<COSObjectable> kidsIter = kids.iterator();
			if (!sParent.equals(field.getPartialName())) {
				sParent = sParent + "." + field.getPartialName();
			}
			while (kidsIter.hasNext()) {
				Object pdfObj = kidsIter.next();
				PDField kid = (PDField) pdfObj;
				if (pdfObj instanceof PDField) {
					setField(kid, sParent);
				}
			}
		} else {
			int index = fieldFullyQualifiedNameList.indexOf(field.getFullyQualifiedName());
			if (index != -1) {
				field.setValue(fieldValueList.get(index + 1));
			}
		}
	}
}
