package ro.kuberam.libs.java.pdf.stamp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.awt.Color;
import org.apache.pdfbox.Overlay;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Stamper {

	private static PDDocument pdfDocument;
	private static int numPages;
	private static Float xVal = null;
	private static Float yVal = null;
	private static String fontFamily = null;
	private static Float fontSize = null;
	private static Color nonStrokingColor = null;
	private static Color strokingColor = null;

	
	public static ByteArrayOutputStream run(InputStream pdfIs, String stampString, Map<String, String> propsMap)
			throws IOException, COSVisitorException {
	
	
		pdfDocument = PDDocument.load(pdfIs, true);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		if(propsMap.containsKey("x")) xVal = Float.parseFloat(config.getProperty("x"));
		if(propsMap.containsKey("y")) yVal = Float.parseFloat(config.getProperty("y"));
		// make sure we have a font
		if(propsMap.containsKey("fontFamily")) {
			fontFamily = propsMap.get("fontFamily");
		} else {
			System.err.println("You must specify a font in the properties map.");
		}
	
		// make sure we have a font size
		if(propsMap.containsKey("fontSize")) {
			fontSize = Float.parseFloat(propsMap.get("fontSize"));
		} else {
			System.err.println("You must specify a font size in the properties map.");
		}
		
		if(propsMap.containsKey("color")) {
			nonStrokingColor = Color.decode(propsMap.get("color"));
		}
		
		
		stampPdf(stampString);
		
		pdfDocument.save(output);
		pdfDocument.close();
	
		return output;
	}
	
	/**
	 * Coordinate the stamping procedure.
	 *
	 */
	public static void stampPdf(String stampString) throws IOException, COSVisitorException {
	
		/*if (pdfDocument.isEncrypted()) {
			try {
				// try to open the encrypted PDF
				pdfDocument.decrypt("");
	
			} catch (InvalidPasswordException e) {
	
				// This error message dictates that the document is encrypted and we have no password
				System.err.println("The document is encrypted or otherwise has prohibitive security settings..");
				System.exit(1);
			}
		}*/
		// create the overlay page with the text to be stamped
		PDDocument overlayDoc = createOverlay(stampString);
	
		// update the number of pages we have in the incoming pdf
		numPages = pdfDocument.getPageCount();
	
		// do the overlay
		doOverlay(overlayDoc);
	
		// close
		overlayDoc.close();
	}
	
	/**
	 * Creates the overlay PDF.
	 *
	 * @param text
	 * @return PDDocument
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	public static PDDocument createOverlay(String text) throws IOException, COSVisitorException {
	
		// Create a document and add a page to it
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);
	
		// Create a new font object selecting one of the PDF base fonts
		PDFont font = PDType1Font.getStandardFont(fontFamily);
	
		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
	
		// Create the text and position it
		contentStream.beginText();
		contentStream.setFont(font, fontSize);
		if(nonStrokingColor != null) {
			contentStream.setNonStrokingColor(nonStrokingColor);
		}
		contentStream.moveTextPositionByAmount(xVal, yVal);
		contentStream.drawString(text);
		contentStream.endText();
	
		// Make sure that the content stream is closed:
		contentStream.close();
	
		//return the string doc
		return document;
	}
	
	/**
	 * Performs the overlay of the two documents.
	 *
	 * @param overlayDoc
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	private static void doOverlay(PDDocument overlayDoc) throws IOException, COSVisitorException {
	
		PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
	
		// get the pages of the pdf
		List pages = docCatalog.getAllPages();
		Iterator pageIter = pages.iterator();
	
		while (pageIter.hasNext()) {
			PDPage page = (PDPage) pageIter.next();
		}
	
		Overlay overlay = new Overlay();
		overlay.overlay(overlayDoc, pdfDocument);
	
		// close, close
		overlayDoc.close();
	}
}
