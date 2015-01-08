package ro.kuberam.libs.java.pdf.metadata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.stream.StreamResult;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

public class Metadata {

	public static StreamResult run(InputStream pdfIs)
			throws IOException, COSVisitorException {

		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
		PDMetadata metadata = catalog.getMetadata();
		StreamResult result=new StreamResult(metadata.createOutputStream());
		
		pdfDocument.close();
		
		return result;
	}
	
	public static ByteArrayOutputStream run(InputStream pdfIs, InputStream xmlmetadata)
			throws IOException, COSVisitorException {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
		PDMetadata metadata = catalog.getMetadata();

		// write new XML metadata
		PDMetadata newMetadata = new PDMetadata(pdfDocument, xmlmetadata, false );
		catalog.setMetadata( newMetadata );
		
		pdfDocument.save(output);
		pdfDocument.close();
		
		return output;
	}

}
