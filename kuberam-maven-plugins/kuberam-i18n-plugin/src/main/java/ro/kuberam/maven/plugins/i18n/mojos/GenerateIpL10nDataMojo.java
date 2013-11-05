package ro.kuberam.maven.plugins.i18n.mojos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.compiler.CompilerMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ro.kuberam.maven.plugins.mojos.KuberamAbstractMojo;
import ro.kuberam.maven.plugins.utils.KuberamMojoUtils;

/**
 * Generates the IP localisation datasets. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius
 *         Teodorescu</a>
 * 
 */

@Mojo(name = "generate-ip-l10n-data")
public class GenerateIpL10nDataMojo extends KuberamAbstractMojo {

	protected static String ip2countryDbUrl = "http://madm.dfki.de/demo/ip-countryside/ip2country.zip";
	protected static String countryCodes2countryNamesDbUrl = "http://opengeocode.org/download/countrynames.txt";
	protected static String cldrDbUrl = "http://unicode.org/Public/cldr/24/core.zip";
	protected File outputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// make output directory
		outputDirectory = KuberamMojoUtils.createDir(new File(projectBuildDirectory + File.separator + "java"));

		// download and unzip the ip2country.db file
		File ip2countryZipFile = KuberamMojoUtils.downloadFromUrl(ip2countryDbUrl, new File(outputDirectory + File.separator + "ip2country.zip"));
		KuberamMojoUtils.extract(ip2countryZipFile, outputDirectory);

		// process the ip2country.db file
		File ip2CountryFile = new File(outputDirectory + File.separator + "ip2country.db");
		parseIpToCountryIsoAlpha2CodeDb(ip2CountryFile);
		ip2countryZipFile.deleteOnExit();

		// download the countrynames.txt file
		File countryCodes2countryNamesFile = KuberamMojoUtils.downloadFromUrl(countryCodes2countryNamesDbUrl, new File(outputDirectory
				+ File.separator + "countrynames.txt"));

		// process the countrynames.txt file
		try {
			parseCountryNamesDb(countryCodes2countryNamesFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// download and unzip the CLDR archive
		File cldrFolder = new File(outputDirectory + File.separator + "core.zip");
		File cldrZipFile = KuberamMojoUtils.downloadFromUrl(cldrDbUrl, cldrFolder);
		KuberamMojoUtils.extract(cldrZipFile, outputDirectory);

		// process the supplementalData.xml file
		parseSupplementalDataFile(new File(cldrFolder + File.separator + "common" + File.separator + "supplemental" + File.separator
				+ "supplementalData.xml"));
		cldrZipFile.deleteOnExit();

		CompilerMojo compiler = new CompilerMojo();
		compiler.setPluginContext(new HashMap());
		compiler.execute();
	}

	public void parseIpToCountryIsoAlpha2CodeDb(File ip2countryFile) {
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(ip2countryFile));
			lnr.skip(Long.MAX_VALUE);
			int recordsNo = lnr.getLineNumber();

			BufferedReader bufRdr = new BufferedReader(new FileReader(ip2countryFile));
			String line = null;
			long[] startRangeIps = new long[recordsNo];
			long[] endRangeIps = new long[recordsNo];
			String[] countryAlpha2Codes = new String[recordsNo];
			int arraysCounter = 0;

			while ((line = bufRdr.readLine()) != null) {
				String[] data = line.split(" ");
				startRangeIps[arraysCounter] = Long.valueOf(data[0].toString());
				endRangeIps[arraysCounter] = Long.valueOf(data[1].toString());
				countryAlpha2Codes[arraysCounter] = data[2].toString();
				arraysCounter++;
			}

			// process the arrays
			int processedArraysCounter = 0;
			int processedRecordsNo = recordsNo * 2;
			long[] processedStartRangeIps = new long[processedRecordsNo];
			String[] processedCountryAlpha2Codes = new String[processedRecordsNo];

			for (int i = 0, il = arraysCounter - 1; i < il; i++) {
				long nextStartIp = startRangeIps[i + 1];
				long incrementedCurrentEndIp = endRangeIps[i] + 1;
				int currentProcessedArraysIndex = i + processedArraysCounter;

				processedStartRangeIps[currentProcessedArraysIndex] = startRangeIps[i];
				processedCountryAlpha2Codes[currentProcessedArraysIndex] = countryAlpha2Codes[i];

				if (nextStartIp != incrementedCurrentEndIp) {
					int nextProcessedArraysIndex = i + processedArraysCounter + 1;
					processedStartRangeIps[nextProcessedArraysIndex] = incrementedCurrentEndIp;
					processedCountryAlpha2Codes[nextProcessedArraysIndex] = "Not assigned";
					processedArraysCounter++;
				}
			}

			// set the last entries in processed arrays
			int trimmedArraysCounter = arraysCounter + processedArraysCounter + 1;
			processedStartRangeIps[trimmedArraysCounter - 2] = startRangeIps[arraysCounter - 1];
			processedCountryAlpha2Codes[trimmedArraysCounter - 2] = countryAlpha2Codes[arraysCounter - 1];
			processedStartRangeIps[trimmedArraysCounter - 1] = endRangeIps[arraysCounter - 1];
			processedCountryAlpha2Codes[trimmedArraysCounter - 1] = "Not assigned";

			long[] trimmedStartRangeIps = Arrays.copyOf(processedStartRangeIps, trimmedArraysCounter);
			String[] trimmedCountryAlpha2Codes = Arrays.copyOf(processedCountryAlpha2Codes, trimmedArraysCounter);

			new ObjectOutputStream(new FileOutputStream(ip2countryFile.getParent() + File.separator + "java" + File.separator + "startRangeIps.ser"))
					.writeObject(trimmedStartRangeIps);
			new ObjectOutputStream(new FileOutputStream(ip2countryFile.getParent() + File.separator + "java" + File.separator
					+ "countryIsoAlpha2Codes.ser")).writeObject(trimmedCountryAlpha2Codes);
		} catch (Exception e) {
		}

	}

	public void parseCountryNamesDb(File countryCodes2countryNamesFile) throws IOException {
		// Metadata (one entry per line)
		// 0 --- ISO 3166-1 alpha-2;
		// 1 --- ISO 3166-1 alpha-3;
		// 2 --- ISO 3166-1 numeric;
		// 3 --- ISO 3166-1 English short name (Gazetteer order);
		// 4 --- ISO 3166-1 English short name (proper reading order);
		// 5 --- ISO 3166-1 English romanized short name (Gazetteer order);
		// 6 --- ISO 3166-1 English romanized short name (proper reading order);
		// 7 --- ISO 3166-1 French short name (Gazetteer order);
		// 8 --- ISO 3166-1 French short name (proper reading order);
		// 9 --- ISO 3166-1 Spanish short name (Gazetteer order);
		// 10 --- UNGEGN English formal name;
		// 11 --- UNGEGN French formal name;
		// 12 --- UNGEGN Spanish formal name;
		// 13 --- UNGEGN Russian cyrillic short name;
		// 14 --- UNGEGN Russian cyrillic formal name;
		// 15 --- UNGEGN local short name;
		// 16 --- UNGEGN local formal name;
		// 17 --- FAO Italian short name (Gazetteer order);
		// 18 --- FAO Italian short name (proper reading order);
		// 19 --- FAO Italian offical name;
		// 20 --- BGN English short name (Gazetteer order);
		// 21 --- BGN English short name (proper reading order);
		// 22 --- BGN English long name;
		// 23 --- BGN local short name;
		// 24 --- BGN local long name;
		// 25 --- PCGN English short name (Gazetteer order);
		// 26 --- PCGN English short name (proper reading order);
		// 27 --- PCGN English long name

		BufferedReader bufRdr = new BufferedReader(new FileReader(countryCodes2countryNamesFile));
		String line = null;

		Properties countryIsoAlpha3CodesProperties = new Properties();
		Properties countryIsoNumericCodesProperties = new Properties();
		Properties countryShortNamesGazetteerOrderEnUsProperties = new Properties();
		Properties countryShortNamesGazetteerOrderFrFrProperties = new Properties();

		while ((line = bufRdr.readLine()) != null) {
			String[] data = line.split("; ");
			String countryAlpha2Code = data[0].toString();

			if (countryAlpha2Code.contains("#")) {
				continue;
			}

			countryIsoAlpha3CodesProperties.setProperty(countryAlpha2Code, data[1].toString());
			countryIsoNumericCodesProperties.setProperty(countryAlpha2Code, data[2].toString());
			countryShortNamesGazetteerOrderEnUsProperties.setProperty(countryAlpha2Code, data[3].toString());
			countryShortNamesGazetteerOrderFrFrProperties.setProperty(countryAlpha2Code, data[7].toString());
		}

		storePropertyFile(countryIsoAlpha3CodesProperties, "country iso alpha-2 code = country iso alpha-3 code", new File(
				countryCodes2countryNamesFile.getParent() + File.separator + "java" + File.separator + "country-iso-alpha-3-codes.properties"));
		storePropertyFile(countryIsoNumericCodesProperties, "country iso alpha-2 code = country iso numeric code", new File(
				countryCodes2countryNamesFile.getParent() + File.separator + "java" + File.separator + "country-iso-numeric-codes.properties"));
		storePropertyFile(countryShortNamesGazetteerOrderEnUsProperties, "country iso alpha-2 code = country short name, gazetteer order, en-US",
				new File(countryCodes2countryNamesFile.getParent() + File.separator + "java" + File.separator
						+ "country-short-names-gazetteer-order-en-US.properties"));
		storePropertyFile(countryShortNamesGazetteerOrderFrFrProperties, "country iso alpha-2 code = country short name, gazetteer order, fr-FR",
				new File(countryCodes2countryNamesFile.getParent() + File.separator + "java" + File.separator
						+ "country-short-names-gazetteer-order-fr-FR.properties"));
	}

	public void parseSupplementalDataFile(File supplementalDataFile) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = null;
		Document doc = null;

		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		try {
			doc = docBuilder.parse(supplementalDataFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = null;
		NodeList nl = null;

		try {
			expr = xpath.compile("//languagePopulation[@officialStatus = 'official']");
			nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		HashSet<String> languageTagHs = new HashSet<String>();

		for (int i = 0, il = nl.getLength(); i < il; i++) {
			org.w3c.dom.Node languagePopulationElement = nl.item(i);

			String languageTag = languagePopulationElement.getAttributes().getNamedItem("type").getNodeValue() + "-"
					+ languagePopulationElement.getParentNode().getAttributes().getNamedItem("type").getNodeValue();
			languageTagHs.add(languageTag);
		}

		try {
			new ObjectOutputStream(new FileOutputStream(outputDirectory + File.separator + "languageTags.ser")).writeObject(languageTagHs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void storePropertyFile(Properties content, String header, File propertiesFile) {
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(propertiesFile);
			content.store(fileOut, header);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
