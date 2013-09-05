package ro.kuberam.maven.plugins.i18n.mojos;

import java.io.File;

import junit.framework.TestCase;

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


public class GenerateIpL10nDataMojoTest extends TestCase {

	private String basedir = PlexusTestCase.getBasedir() + File.separator;
	private String projectBuildDirectory = basedir + File.separator + "target";

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
	public void testMojoGoal() throws Exception {
		final GenerateIpL10nDataMojo mojo = this.mojo();
		mojo.execute();
	}

	private GenerateIpL10nDataMojo mojo() throws Exception {
		final GenerateIpL10nDataMojo mojo = new GenerateIpL10nDataMojo();

		mojo.setProject(new MavenProject());
		mojo.setRepoSession(newSession(newRepositorySystem()));
		mojo.setProjectBuildDirectory(new File(projectBuildDirectory));

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
