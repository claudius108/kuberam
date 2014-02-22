package ro.kuberam.libs.java.pdf.populateForm;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.junit.Test;

public class PopulateFormTest {

	@Test
	public void testExtractFields() throws IOException {

		InputStream pdfIs = this.getClass().getResourceAsStream("sf702.pdf");

		PDDocument pdfDocument = PDDocument.load(pdfIs, true);

		PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();

		List fields = acroForm.getFields();
		Iterator fieldsIter = fields.iterator();

		System.out.println(new Integer(fields.size()).toString()
				+ " top-level fields were found on the form");

		while (fieldsIter.hasNext()) {
			PDField field = (PDField) fieldsIter.next();
			processField(field, "|--", field.getPartialName());
		}

		// while (fieldsIter.hasNext()) {
		// PDField field = (PDField) fieldsIter.next();
		// processField(field, field.getPartialName());
		// List subFields = field.getAcroForm().getFields();
		// Iterator subFieldsIter = subFields.iterator();
		// while (subFieldsIter.hasNext()) {
		// PDField subSubField = (PDField) subFieldsIter.next();
		// processField(subSubField, subSubField.getPartialName());
		// }
		// }

		// FDFDocument fdf = acroForm.exportFDF();
		// StringWriter sw = new StringWriter();
		// try {
		// fdf.saveXFDF(sw);
		// // fdf.saveXFDF(System.out);
		// } catch (COSVisitorException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(sw);

		// if (form != null) {
		// List<PDField> fields = form.getFields();
		// for (int i = 0; i < fields.size(); i++) {
		// // System.out.println("---");
		// // System.out.println("FieldType: " +
		// fields.get(i).getPartialName());
		// }
		// }
		// COSDictionary trailer = pdf.getDocument().getTrailer();
		// COSDictionary root = (COSDictionary)
		// trailer.getDictionaryObject(COSName.ROOT);
		// COSDictionary acroForm = (COSDictionary)
		// root.getDictionaryObject(COSName.getPDFName("AcroForm"));
		// if (null != acroForm) {
		// COSArray fields1 = (COSArray)
		// acroForm.getDictionaryObject(COSName.getPDFName("Fields"));
		// for (int l = 0; l < fields1.size(); l++) {
		// COSDictionary field = (COSDictionary) fields1.getObject(l);
		// System.out.println("FieldType: " + field.toString());
		// COSArray rectArray = (COSArray) field.getDictionaryObject("Rect");
		// PDRectangle mediaBox = new PDRectangle(rectArray);
		// System.out.println("mediaBox: " + mediaBox.getLowerLeftX() + "||"
		// + mediaBox.getLowerLeftY());
		// System.out.println("mediaBox: " + mediaBox.getUpperRightX() + "||"
		// + mediaBox.getUpperRightY());
		// }
		// }

		pdfDocument.close();

	}

	private void processFields2(List<COSObjectable> fields) {
		Iterator kidsIter = fields.iterator();

		while (kidsIter.hasNext()) {
			Object pdfObj = kidsIter.next();
			if (pdfObj instanceof PDField) {
				PDField kid = (PDField) pdfObj;
				try {
					processFields2(kid.getKids());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// while (fieldsIter.hasNext()) {
		// PDField field = (PDField) fieldsIter.next();
		// System.out.println(field.getPartialName());
		// try {
		// processFields2(field.getKids());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}

	private void processField(PDField field, String sLevel, String sParent) throws IOException {
		List kids = field.getKids();
		if (kids != null) {
			Iterator kidsIter = kids.iterator();
			if (!sParent.equals(field.getPartialName())) {
				sParent = sParent + "." + field.getPartialName();
			}
			System.out.println(sLevel + sParent);
			// System.out.println(sParent + " is of type " +
			// field.getClass().getName());
			while (kidsIter.hasNext()) {
				Object pdfObj = kidsIter.next();
				PDField kid = (PDField) pdfObj;
				processField(kid, "|  " + sLevel, sParent);				
//				if (pdfObj instanceof PDField) {
//					PDField kid = (PDField) pdfObj;
//					processField(kid, "|  " + sLevel, sParent);
//				}
			}
		} else {
			String outputString = sLevel + sParent + "." + field.getPartialName() + " = "
					+ field.getValue() + ",  type=" + field.getClass().getName();

			System.out.println(outputString);
		}
	}
}
