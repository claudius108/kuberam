package ro.kuberam.maven.plugins.xml.mojos;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ro.kuberam.maven.plugins.mojos.KuberamAbstractMojo;

/**
 * Process XInclude for the input XML files. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius
 *         Teodorescu</a>
 * 
 */

@Mojo(name = "process-xinclude")
public class ProcessXincludeMojo extends KuberamAbstractMojo {

	/**
	 * The input file.
	 * 
	 * @parameter
	 * 
	 */
	@Parameter(required = true)
	private File inputFile;

	/**
	 * The parameter for setting the output of the XML declaration in the
	 * resulting file.
	 * 
	 * @parameter
	 * 
	 */
	@Parameter(defaultValue = "no")
	private String omitXmlDeclaration = "no";

	/**
	 * Output directory.
	 * 
	 * @parameter
	 * 
	 */
	@Parameter(required = true)
	private File outputDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		FileUtils.mkdir(outputDir.getAbsolutePath());

		String inputFileName = FileUtils.filename(inputFile.getAbsolutePath());

		InputStream xmlStream = null;
		try {
			xmlStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setXIncludeAware(true);
		factory.setNamespaceAware(true);
		try {
			factory.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", false);
		} catch (ParserConfigurationException e3) {
			e3.printStackTrace();
		}
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e2) {
			e2.printStackTrace();
		}
		if (!docBuilder.isXIncludeAware()) {
			throw new IllegalStateException();
		}

		Document doc = null;
		try {
			doc = docBuilder.parse(xmlStream);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// print result
		Source source = new DOMSource(doc);
		Result result = new StreamResult(new File(outputDir + File.separator + inputFileName));
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
