package ro.kuberam.libs.java.pdf.formControls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

public class GetTextFields {

	private static XMLStreamWriter writer;

	public static ByteArrayOutputStream run(InputStream pdfIs) throws IOException, XMLStreamException {

		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

		List<PDField> fields = acroForm.getFields();
		Iterator<PDField> fieldsIter = fields.iterator();

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		ByteArrayOutputStream oxygenParserOutput = new ByteArrayOutputStream();
		writer = factory.createXMLStreamWriter(oxygenParserOutput);

		writer.writeStartElement("xfdf");
		writer.writeAttribute("xmlns", "http://ns.adobe.com/xfdf/");
		writer.writeStartElement("fields");

		while (fieldsIter.hasNext()) {
			PDField field = fieldsIter.next();
			processField(field, field.getPartialName());
		}

		writer.writeEndElement();
		writer.writeEndElement();
		writer.flush();

		pdfDocument.close();

		System.out.println();

		return oxygenParserOutput;
	}

	private static void processField(final PDField field, String sParent) throws IOException,
			XMLStreamException {
		List<COSObjectable> kids = field.getKids();
		if (kids != null) {
			Iterator<COSObjectable> kidsIter = kids.iterator();
			if (!sParent.equals(field.getPartialName())) {
				sParent = sParent + "." + field.getPartialName();
			}
			// System.out.println(sParent + " is of type " +
			// field.getClass().getName());
			while (kidsIter.hasNext()) {
				Object pdfObj = kidsIter.next();
				PDField kid = (PDField) pdfObj;
				if (pdfObj instanceof PDField) {
					processField(kid, sParent);
				}
			}
		} else {
			writer.writeStartElement("field");
			writer.writeAttribute("name", field.getFullyQualifiedName());
			writer.writeStartElement("value");
			writer.writeCharacters(field.getValue());
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}
}
