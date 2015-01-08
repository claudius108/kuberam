package ro.kuberam.libs.java.pdf.stamp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
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
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSDictionary;
import com.steadystate.css.parser.CSSOMParser;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleDeclaration;

public class Stamper {
	private static PDDocument pdfDocument;
	private static String selector;
	
	public static ByteArrayOutputStream run(InputStream pdfIs, String stamp, String stampStyleDeclaration)
			throws IOException, COSVisitorException {
	
		pdfDocument = PDDocument.load(pdfIs, true);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		CSSOMParser parser = new CSSOMParser();
		InputSource inputSource = new InputSource(new StringReader(stampStyleDeclaration));
		CSSStyleDeclaration style = parser.parseStyleDeclaration(inputSource);

		// create the overlay page with the text to be stamped
		PDDocument overlayDoc = createOverlayFromString(stamp, style);
		// do the overlay
		doOverlay(overlayDoc);
		// close
		overlayDoc.close();
		
		pdfDocument.save(output);
		pdfDocument.close();
	
		return output;
	}
	
	public static ByteArrayOutputStream run(InputStream pdfIs, String stamp, String stampSelector, String stampStyling)
			throws IOException, COSVisitorException {
	
		pdfDocument = PDDocument.load(pdfIs, true);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		CSSOMParser parser = new CSSOMParser();
		InputSource inputSource = new InputSource(new StringReader(stampStyling));
		CSSStyleSheet stylesheet = parser.parseStyleSheet(inputSource,null,null);
		CSSRuleList ruleList = stylesheet.getCssRules();
		List<CSSStyleRule> rules = new ArrayList();
		for (int i=0; i < ruleList.getLength(); i++) {
			CSSRule rule = ruleList.item(i);
            if(rule instanceof CSSStyleRule) {
            	CSSStyleRule styleRule=(CSSStyleRule)rule;
            	rules.add(styleRule);
            }
	    }
		// for now just use single rule
		CSSStyleDeclaration style = rules.get(0).getStyle();
		// create the overlay page with the text to be stamped
		PDDocument overlayDoc = createOverlayFromString(stamp, style);
		// do the overlay
		doOverlay(overlayDoc);
		// close
		overlayDoc.close();
		
		pdfDocument.save(output);
		pdfDocument.close();
	
		return output;
	}
	
	/**
	 * Creates the overlay PDF.
	 *
	 * @param text
	 * @param ruleList
	 * @return PDDocument
	 * @throws IOException
	 * @throws COSVisitorException
	 */
	public static PDDocument createOverlayFromString(String text, CSSStyleDeclaration style) throws IOException, COSVisitorException {
		float x = Float.parseFloat(style.getPropertyValue("left"));
		float y = Float.parseFloat(style.getPropertyValue("top"));
		String fontFamily = style.getPropertyValue("font-family");
		float fontSize = Float.parseFloat(style.getPropertyValue("font-size"));
		String color = style.getPropertyValue("color").toString();
		Color nonStrokingColor = new Color(0,0,0);
		if(color.matches("^rgb")) {
			String[] parts = color.replace("^rgba?\\(([0-9,\\s]+)\\)$","$1").split(",");
			float r = Float.parseFloat(parts[0]);
			float g = Float.parseFloat(parts[1]);
			float b = Float.parseFloat(parts[2]);
			float a = parts.length > 3 ? Float.parseFloat(parts[3]) : 1;
			nonStrokingColor = new Color(r,g,b,a);
		} else {
			System.err.println("Only HEX and RGB(A) colors are allowed. "+color);
		}
	
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
		
		contentStream.moveTextPositionByAmount(x, y);
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
	
		// get the pages of the pdf
		List<PDPage> allPages = pdfDocument.getDocumentCatalog().getAllPages();
		String page = "all";
		//if(selector.matches("@page\\s*:")) {
		//	page = selector.replace("@page\\s*:(\\w)","$1");
		//}
		// default=stamp all
		if(page != "first") {
			clonePages(overlayDoc,allPages.size());
		}
		Overlay overlay = new Overlay();
		overlay.overlay(overlayDoc, pdfDocument);
	}
	
	private static void clonePages(PDDocument doc, int count) throws COSVisitorException {
		List<PDPage> allPages = doc.getDocumentCatalog().getAllPages();

		PDPage page = allPages.get(0);
		COSDictionary pageDict = page.getCOSDictionary();
		COSDictionary newPageDict = pageDict; new COSDictionary(pageDict);

		newPageDict.removeItem(COSName.ANNOTS);

		PDPage newPage = new PDPage(newPageDict);
		for(int i = 0; i < count; i++) {
			doc.addPage(newPage);
		}
	}
}
