package ro.kuberam.maven.plugins.i18n.mojos;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Test;

import ro.kuberam.maven.plugins.utils.KuberamAbstractMojoTestBase;
import ro.kuberam.maven.plugins.utils.KuberamMojoUtils;

public class GenerateIpL10nDataMojoTest extends KuberamAbstractMojoTestBase {

	private File ip2countryZipFile = new File(projectBuildDirectory + "ip2country.zip");
	private File countryCodes2countryNameFile = new File(projectBuildDirectory + "countrynames.txt");
	private File cldrZipFile = new File(projectBuildDirectory + "core.zip");
	private File cldrFolder = new File(projectBuildDirectory + "cldr");
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
		KuberamMojoUtils.extract(ip2countryZipFile, new File(projectBuildDirectory));
	}

	@Test
	public void testDownloadCountryCodesToCountryNamesDb() throws Exception {
		KuberamMojoUtils.downloadFromUrl(GenerateIpL10nDataMojo.countryCodes2countryNamesDbUrl, countryCodes2countryNameFile);
	}

	@Test
	public void testParseIpToCountryIsoAlpha2CodeDbFunction() throws Exception {
		final GenerateIpL10nDataMojo mojo = this.mojo();
		KuberamMojoUtils.extract(ip2countryZipFile, new File(projectBuildDirectory));
		mojo.parseIpToCountryIsoAlpha2CodeDb(new File(projectBuildDirectory + "ip2country.db"));
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
	public void testParseSupaetherVersionplementalDataFileFunction() throws Exception {
		final GenerateIpL10nDataMojo mojo = this.mojo();
		mojo.outputDirectory = new File(projectBuildDirectory + "java");
		mojo.parseSupplementalDataFile(supplementalDataFile);
	}

	@Test
	public void testMojoGoal() throws Exception {
		final GenerateIpL10nDataMojo mojo = this.mojo();
		mojo.execute();
	}

	private GenerateIpL10nDataMojo mojo() throws Exception {
		final GenerateIpL10nDataMojo mojo = new GenerateIpL10nDataMojo();

		mojo.setProjectBuildDirectory(new File(projectBuildDirectory));
		mojo.setProject(new MavenProject());
		mojo.setRepoSession(newSession(newRepositorySystem()));

		return mojo;
	}

}
