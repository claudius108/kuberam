package ro.kuberam.maven.xarPlugin.tests;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import ro.kuberam.maven.xarPlugin.GenerateDescriptorsMojo;

public class GenerateDescriptorsMojos extends AbstractMojoTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// code
	}

	@Override
	protected void tearDown() throws Exception {
		// code
		super.tearDown();
	}

	/**
	 * tests the proper discovery and configuration of the mojo
	 * 
	 * @throws Exception
	 */
	public void generateDescriptorsMojoTest() throws Exception {

		GenerateDescriptorsMojos mojo = getGenerateDescriptorsMojo("src/test/resources/ro/kuberam/maven/xarPlugin/tests/plugin-config.xml");

		//mojo.execute();
	}

	private GenerateDescriptorsMojos getGenerateDescriptorsMojo(String pomXml) throws Exception {
		File testPom = new File(getBasedir(), pomXml);

		GenerateDescriptorsMojos mojo = (GenerateDescriptorsMojos) lookupMojo("generate-descriptors", testPom);

		assertNotNull(mojo);

		return mojo;
	}

}
