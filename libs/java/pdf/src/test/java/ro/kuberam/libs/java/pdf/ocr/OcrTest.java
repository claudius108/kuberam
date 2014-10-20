package ro.kuberam.libs.java.pdf.ocr;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.junit.Test;

public class OcrTest {

	@Test
	public void test1() throws IOException, XMLStreamException {

        File imageFile = new File("/home/claudius/workspaces/repositories/git/kuberam/libs/java/pdf/src/test/resources/ro/kuberam/libs/java/pdf/ocr/sample-1.jpg");
        Tesseract instance = Tesseract.getInstance();  // JNA Interface Mapping
        // Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping

        try {
        	System.out.println(imageFile.exists());
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        
	}

}

