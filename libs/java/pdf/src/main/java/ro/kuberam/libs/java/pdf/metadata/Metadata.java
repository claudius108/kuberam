package ro.kuberam.libs.java.pdf.metadata;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.stream.StreamResult;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class Metadata {

	public static String get(InputStream pdfIs)
			throws IOException, COSVisitorException {

		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
		PDMetadata metadata = catalog.getMetadata();
		byte[] bStream = metadata.getByteArray();
		String result = new String(bStream, "UTF-8");
		pdfDocument.close();
		
		return result;
	}
	
	public static ByteArrayOutputStream set(InputStream pdfIs, InputStream xmlmetadata)
			throws IOException, COSVisitorException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
		PDMetadata metadata = catalog.getMetadata();

		// write new XML metadata
		PDMetadata newMetadata = new PDMetadata(pdfDocument, xmlmetadata, true);
		catalog.setMetadata( newMetadata );
		
		//PDStream stream=new PDStream(pdfDocument);
		//stream.addCompression();
		
		pdfDocument.save(output);
		pdfDocument.close();
		return output;
	}

}
