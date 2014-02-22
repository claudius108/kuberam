package org.expath.libs.pdf;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

public class PdfToImageTest {
	
	private Image image;
	private String targetDirPath = "/home/claudius/workspaces/institutul-de-lingvistica/backup/DEX var 2012 pdf/";

	@Test
	public void test1() throws IOException {
		InputStream is = new FileInputStream(targetDirPath + "G.pdf");

		PDDocument pdf = PDDocument.load(is, true);
		List<PDPage> pages = pdf.getDocumentCatalog().getAllPages();

		int counter = 0;

		for (PDPage page : pages) {
			BufferedImage bufferedImage = page.convertToImage();
			File outputfile = new File(targetDirPath + counter + ".jpg");
			ImageIO.write(bufferedImage, "jpg", outputfile);
			counter++;
		}
	}

}
