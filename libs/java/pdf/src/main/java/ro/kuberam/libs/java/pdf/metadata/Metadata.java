package ro.kuberam.libs.java.pdf.metadata;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.lang.reflect.Method;
import javax.xml.transform.stream.StreamResult;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class Metadata {
	
	private static String toCamelCase(String s){
		String[] parts = s.split("-");
		String camelCaseString = "";
		for (String part : parts){
			camelCaseString = camelCaseString + toProperCase(part);
		}
		return camelCaseString;
	}

	private static String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String get-document-xmp(InputStream pdfIs)
			throws IOException, COSVisitorException {

		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
		PDMetadata metadata = catalog.getMetadata();
		byte[] bStream = metadata.getByteArray();
		String result = new String(bStream, "UTF-8");
		pdfDocument.close();
		
		return result;
	}
	
	public static ByteArrayOutputStream set-document-xmp(InputStream pdfIs, InputStream xmlmetadata)
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
	
	public static String get-document-info(InputStream pdfIs, String[] properties)
		throw IOException, COSVisitorException {
		
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentInformation information = pdfDocument.getDocumentInformation();
		String[] availProps = { "author", "creator", "keywords", "producer", "subject", "title", "trapped", "creation-date", "modification-date" };

		ArrayList<Object> result = new ArrayList<Object>();
		
		for(String prop : properties) {
			if(availProps.contains(props)) {
				Method method = PDDocumentInformation.class.getMethod("get" + toCamelCase(prop));
				result.add(method.invoke(information));
			}
		}
		return result;
		/*for (Map.Entry<String, String> entry : map.entrySet()){
			
			Method method = PDDocumentInformation.class.getMethod("get" + toCamelCase(prop));
			entry.getKey() entry.getValue();
		}*/
	}

}
