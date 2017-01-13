package ro.kuberam.libs.java.pdf.metadata;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
import org.apache.pdfbox.cos.COSName; 

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

	public static String getDocumentXMP(InputStream pdfIs)
			throws IOException, COSVisitorException {
		String result = null;
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		try {
			PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
			PDMetadata metadata = catalog.getMetadata();
			byte[] bStream = metadata.getByteArray();
			result = new String(bStream, "UTF-8");
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			pdfDocument.close();
		}
		return result;
	}
	
	public static ByteArrayOutputStream setDocumentXMP(InputStream pdfIs, InputStream xmlmetadata)
			throws IOException, COSVisitorException {
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		
		try {
			PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
			PDMetadata metadata = catalog.getMetadata();
	
			// write new XML metadata
			PDMetadata newMetadata = new PDMetadata(pdfDocument, xmlmetadata, true);
			catalog.setMetadata( newMetadata );
			
			//PDStream stream=new PDStream(pdfDocument);
			//stream.addCompression();
			
			pdfDocument.save(output);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			pdfDocument.close();
		}
		return output;
	}
	
	public static ArrayList<String> getDocumentInfo(InputStream pdfIs, ArrayList<String> properties)
			throws IOException, COSVisitorException {
		
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentInformation information = pdfDocument.getDocumentInformation();

		ArrayList<String> result = new ArrayList<String>();
		
		Calendar cal = null;
		
		try {
			for(String prop : properties) {
				String custom = "";
				String val = null;
				if(prop.matches("^custom-.*")) {
					custom = toCamelCase(prop.replaceAll("^custom-",""));
				}
				if(custom != "") {
					val = information.getCustomMetadataValue(custom);
				} else {
					/*Method method = null;
					try {
						method = PDDocumentInformation.class.getMethod("get" + toCamelCase(prop));
					} catch(NoSuchMethodException e) {
						e.printStackTrace();
					}
					if(method != null) {
						try {
							if(prop == "creation-date" || prop == "modification-date") {
								Calendar cal = (Calendar) method.invoke(information);
								val = DatatypeConverter.printDateTime(cal);
							} else {
								val = (String) method.invoke(information);
							}
						} catch(IllegalAccessException e) {
							e.printStackTrace();
						} catch(InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					private final static String[] availProps = { "author", "creator", "keywords", "producer", "subject", "title", "trapped", "creation-date", "modification-date" };
					*/
					switch(prop) {
						case "author":
							val = (String) information.getAuthor();
						break;
						case "creator":
							val = (String) information.getCreator();
						break;
						case "keywords":
							val = (String) information.getKeywords();
						break;
						case "producer":
							val = (String) information.getProducer();
						break;
						case "subject":
							val = (String) information.getSubject();
						break;
						case "title":
							val = (String) information.getTitle();
						break;
						case "trapped":
							val = (String) information.getTrapped();
						break;
						case "creation-date":
							cal = (Calendar) information.getCreationDate();
							val = (String) DatatypeConverter.printDateTime(cal);
						break;
						case "modification-date":
							cal = (Calendar) information.getModificationDate();
							val = (String) DatatypeConverter.printDateTime(cal);
						break;
						default:
							throw new IllegalArgumentException("Property not available!");
					}
				}
				result.add(val);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			pdfDocument.close();
		}
		return result;
	}
	
	public static ByteArrayOutputStream setDocumentInfo(InputStream pdfIs, ArrayList<String> properties, ArrayList<String> values)
			throws IOException, COSVisitorException {
		
		PDDocument pdfDocument = PDDocument.load(pdfIs, true);
		PDDocumentInformation information = pdfDocument.getDocumentInformation();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try {
			for(int i = 0; i < properties.size(); i++) {
				String prop = properties.get(i);
				String val = null;
				String custom = "";
				if(prop.matches("^custom-.*")) {
					custom = toCamelCase(prop.replaceAll("^custom-",""));
				}
				val = values.get(i);
				if(val != null) {
					if(custom != "") {
						information.setCustomMetadataValue(custom,val);
					} else {
						/*Method method = null;
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
						*/
						switch(prop) {
							case "author":
								information.setAuthor(val);
							break;
							case "creator":
								information.setCreator(val);
							break;
							case "keywords":
								information.setKeywords(val);
							break;
							case "producer":
								information.setProducer(val);
							break;
							case "subject":
								information.setSubject(val);
							break;
							case "title":
								information.setTitle(val);
							break;
							case "trapped":
								information.setTrapped(val);
							break;
							case "creation-date":
								information.setCreationDate(DatatypeConverter.parseDateTime(val));
							break;
							case "modification-date":
								information.setModificationDate(DatatypeConverter.parseDateTime(val));
							break;
							default:
								throw new IllegalArgumentException("Property not available!");
						}
					}
				}
			}
			pdfDocument.setDocumentInformation(information);
			pdfDocument.save(output);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			pdfDocument.close();
		}
		// start postprocessing
		PDStream stream=new PDStream(new PDDocument(), new ByteArrayInputStream( output.toByteArray() ));
        stream.addCompression();
		byte[] bytes = new byte[0];
		try {
			bytes = stream.getByteArray();
		} catch(IOException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		out.write(bytes, 0, bytes.length);
		return out;
	}
}
