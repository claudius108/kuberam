package ro.kuberam.libs.java.pdf.contentRasterization;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.fonts.FontMappings;
import org.junit.Test;

public class ToImageTest {

	private File pdfFilePath = new File(
			"/home/claudius/workspaces/repositories/git/kuberam/libs/java/pdf/src/test/resources/ro/kuberam/libs/java/pdf/frus1969-76v19p1.pdf");
	private File targetDirPath = new File("/home/claudius/");

	@Test
	public void testPdfbox() throws IOException {
		InputStream pdfIs = this.getClass().getResourceAsStream("../frus1969-76v19p1.pdf");

		PDDocument pdf = PDDocument.load(pdfIs, true);
		List<PDPage> pages = pdf.getDocumentCatalog().getAllPages();

		BufferedImage image = pages.get(0).convertToImage();

		File imageFile = new File(targetDirPath.getAbsolutePath() + File.separator + "export-by-pdfbox.png");

		ImageIO.write(image, "png", imageFile);
		image.flush();

		pdf.close();

	}

	@Test
	public void testJpedal() throws IOException {
		/** instance of PdfDecoder to convert PDF into image */
		PdfDecoder decode_pdf = new PdfDecoder(true);

		/** set mappings for non-embedded fonts to use */
		FontMappings.setFontReplacements();

		/** open the PDF file - can also be a URL or a byte array */
		try {
			decode_pdf
					.openPdfFile("/home/claudius/workspaces/repositories/git/kuberam/libs/java/pdf/src/test/resources/ro/kuberam/libs/java/pdf/frus1969-76v19p1.pdf"); // file
			// decode_pdf.openPdfFile("C:/myPDF.pdf", "password"); //encrypted
			// file
			// decode_pdf.openPdfArray(bytes); //bytes is byte[] array with PDF
			// decode_pdf.openPdfFileFromURL("http://www.mysite.com/myPDF.pdf",false);

			/** get page 1 as an image */
			// page range if you want to extract all pages with a loop
			int start = 217, end = 217;
			for (int i = start; i < end + 1; i++) {
				BufferedImage img = decode_pdf.getPageAsImage(i);
				File outputfile = new File(targetDirPath.getAbsolutePath() + File.separator
						+ "export-by-jpedal.png");
				ImageIO.write(img, "png", outputfile);
			}

			/** close the pdf file */
			decode_pdf.closePdfFile();

		} catch (PdfException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testICEpdf() {
		// open the file
		Document document = new Document();
		try {
			document.setFile(pdfFilePath.getAbsolutePath());
		} catch (PDFException ex) {
			System.out.println("Error parsing PDF document " + ex);
		} catch (PDFSecurityException ex) {
			System.out.println("Error encryption not supported " + ex);
		} catch (FileNotFoundException ex) {
			System.out.println("Error file not found " + ex);
		} catch (IOException ex) {
			System.out.println("Error IOException " + ex);
		}

		// save page captures to file.
		float scale = 1.0f;
		float rotation = 0f;

		BufferedImage image = (BufferedImage) document.getPageImage(216, GraphicsRenderingHints.PRINT,
				Page.BOUNDARY_CROPBOX, rotation, scale);
		RenderedImage rendImage = image;
		try {
			File file = new File(targetDirPath.getAbsolutePath() + File.separator + "export-by-icepdf.png");
			ImageIO.write(rendImage, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		image.flush();
		document.dispose();
	}
}
