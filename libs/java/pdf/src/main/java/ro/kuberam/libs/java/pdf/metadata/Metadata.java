package ro.kuberam.libs.java.pdf.metadata;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Calendar;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.DatatypeConverter;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDStream;

public class Metadata {
	
	private final static String[] availProps = { "author", "creator", "keywords", "producer", "subject", "title", "trapped", "creation-date", "modification-date" };
	
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

	public static String getDocumentXMP(InputStream pdfIs)
			throws IOException, COSVisitorException {

		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
		PDMetadata metadata = catalog.getMetadata();
		byte[] bStream = metadata.getByteArray();
		String result = new String(bStream, "UTF-8");
		pdfDocument.close();
		
		return result;
	}
	
	public static ByteArrayOutputStream setDocumentXMP(InputStream pdfIs, InputStream xmlmetadata)
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
	
	public static ArrayList<String> getDocumentInfo(InputStream pdfIs, ArrayList<String> properties)
			throws IOException, COSVisitorException {
		
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentInformation information = pdfDocument.getDocumentInformation();

		ArrayList<String> result = new ArrayList<String>();
		
		for(String prop : properties) {
			if(Arrays.asList(availProps).contains(prop)) {
				Method method = null;
				try {
					method = PDDocumentInformation.class.getMethod("get" + toCamelCase(prop));
				} catch(NoSuchMethodException e) {
					e.printStackTrace();
				}
				if(method != null) {
					try {
						String val = null;
						if(prop == "creation-date" || prop == "modification-date") {
							Calendar cal = (Calendar) method.invoke(information);
							val = DatatypeConverter.printDateTime(cal);
						} else {
							val = (String) method.invoke(information);
						}
						result.add(val);
					} catch(IllegalAccessException e) {
						e.printStackTrace();
					} catch(InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			} else {
				throw new IllegalArgumentException("Property not available!");
			}
		}
		pdfDocument.close();
		return result;
	}
	
	public static ByteArrayOutputStream setDocumentInfo(InputStream pdfIs, ArrayList<String> properties, ArrayList<String> values)
			throws IOException, COSVisitorException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentInformation information = pdfDocument.getDocumentInformation();
		
		for(int i = 0; i < properties.size(); i++) {
			String prop = properties.get(i);
			String val = null;
			try {
				val = values.get(i);
			} catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			if(val != null) {
				if(Arrays.asList(availProps).contains(prop)) {
					Method method = null;
					Class[] cls = null;
					if(prop == "creation-date" || prop == "modification-date") {
						cls = new Class[]{Calendar.class};
					} else {
						cls = new Class[]{String.class};
					}
					try {
						method = PDDocumentInformation.class.getMethod("set" + toCamelCase(prop), cls);
					} catch(NoSuchMethodException e) {
						e.printStackTrace();
					}
					if(method != null) {
						try {
							if(prop == "creation-date" || prop == "modification-date") {
								method.invoke(information, DatatypeConverter.parseDateTime(val));
							} else {
								method.invoke(information, val);
							}
						} catch(IllegalAccessException e) {
							e.printStackTrace();
						} catch(InvocationTargetException e) {
							e.printStackTrace();
						}
					} else {
						throw new IllegalArgumentException("Method empty!");
					}
				} else {
					throw new IllegalArgumentException("Property not available!");
				}
			} else {
				throw new IllegalArgumentException("Value empty!");
			}
		}
		pdfDocument.setDocumentInformation(information);
		pdfDocument.save(output);
		pdfDocument.close();
		return output;
	}
}
