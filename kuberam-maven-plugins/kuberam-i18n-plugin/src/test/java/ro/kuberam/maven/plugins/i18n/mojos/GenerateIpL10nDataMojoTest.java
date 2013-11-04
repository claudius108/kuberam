package ro.kuberam.maven.plugins.i18n.mojos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.PlexusTestCase;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.junit.Test;

import ro.kuberam.maven.plugins.utils.KuberamMojoUtils;

public class GenerateIpL10nDataMojoTest extends PlexusTestCase {

	private String basedir = PlexusTestCase.getBasedir() + File.separator;
	private File projectBuildDirectory = new File(basedir + File.separator + "target");
	private File ip2countryZipFile = new File(projectBuildDirectory + File.separator + "ip2country.zip");
	private File countryCodes2countryNameFile = new File(projectBuildDirectory + File.separator + "countrynames.txt");
	private File cldrZipFile = new File(projectBuildDirectory + File.separator + "core.zip");
	private File cldrFolder = new File(projectBuildDirectory + File.separator + "cldr");
	private File supplementalDataFile = new File(cldrFolder + File.separator + "common" + File.separator + "supplemental" + File.separator
			+ "supplementalData.xml");

	// @Test
	// @Ignore
	// public void testSimpleProjectBuild() throws Exception {
	// executeMojo("src/test/resources/ro/kuberam/maven/plugins/i18n/mojos/generate-ip-l10n-data/plugin-config.xml");
	// }

	// @Test
	// public void testSettingMojoVariables() throws Exception {
	// GenerateIpL10nDataMojo mojo = new GenerateIpL10nDataMojo();
	//
	// setVariableValueToObject(mojo, "keyOne", "myValueOne");
	//
	// assertEquals("myValueOne", (String) getVariableValueFromObject(mojo,
	// "keyOne"));
	//
	// }

	// private GenerateIpL10nDataMojo getMojo(String pluginXml) throws Exception
	// {
	// return (GenerateIpL10nDataMojo) lookupMojo("generate-ip-l10n-data",
	// basedir + "/src/test/plugin-configs/assembly/" + pluginXml);
	// }
	//
	// private GenerateIpL10nDataMojo executeMojo(String pluginXml) throws
	// Exception {
	// GenerateIpL10nDataMojo mojo = getMojo(pluginXml);
	//
	// mojo.execute();
	//
	// return mojo;
	// }

	@Test
	public void testDownloadIpToCountryDb() throws Exception {
		KuberamMojoUtils.downloadFromUrl(GenerateIpL10nDataMojo.ip2countryDbUrl, ip2countryZipFile);
	}

	@Test
	public void testExtractIpToCountryDb() throws Exception {
		KuberamMojoUtils.extract(ip2countryZipFile, projectBuildDirectory);
	}

	@Test
	public void testDownloadCountryCodesToCountryNamesDb() throws Exception {
		KuberamMojoUtils.downloadFromUrl(GenerateIpL10nDataMojo.countryCodes2countryNamesDbUrl, countryCodes2countryNameFile);
	}

	@Test
	public void testParseIpToCountryIsoAlpha2CodeDbFunction() throws Exception {
		final GenerateIpL10nDataMojo mojo = this.mojo();
		KuberamMojoUtils.extract(ip2countryZipFile, projectBuildDirectory);
		mojo.parseIpToCountryIsoAlpha2CodeDb(new File(projectBuildDirectory + File.separator + "ip2country.db"));
	}

	@Test
	public void testParseCountryNamesDbFunction() throws Exception {
		final GenerateIpL10nDataMojo mojo = this.mojo();
		mojo.parseCountryNamesDb(countryCodes2countryNameFile);
	}

	@Test
	public void testDownloadCldrDb() throws Exception {
		KuberamMojoUtils.downloadFromUrl(GenerateIpL10nDataMojo.cldrDbUrl, cldrZipFile);
	}

	@Test
	public void testExtractCldrDb() throws Exception {
		KuberamMojoUtils.extract(cldrZipFile, cldrFolder);
	}

	@Test
	public void testParseSupplementalDataFileFunction() throws Exception {
		final GenerateIpL10nDataMojo mojo = this.mojo();
		mojo.outputDirectory = new File(projectBuildDirectory + File.separator + "java");
		mojo.parseSupplementalDataFile(supplementalDataFile);
	}


	@Test
	public void testMojoGoal() throws Exception {
		final GenerateIpL10nDataMojo mojo = this.mojo();
		mojo.execute();
	}

	private GenerateIpL10nDataMojo mojo() throws Exception {
		final GenerateIpL10nDataMojo mojo = new GenerateIpL10nDataMojo();

		mojo.setProjectBuildDirectory(projectBuildDirectory);
		mojo.setProject(new MavenProject());
		mojo.setRepoSession(newSession(newRepositorySystem()));

		return mojo;
	}

	private static RepositorySystemSession newSession(RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		LocalRepository localRepo = new LocalRepository("target/local-repo");
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

		return session;
	}

	private static RepositorySystem newRepositorySystem() {
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

		return locator.getService(RepositorySystem.class);
	}
}
