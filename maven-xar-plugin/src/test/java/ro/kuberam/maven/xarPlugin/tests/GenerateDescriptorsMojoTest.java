package ro.kuberam.maven.xarPlugin.tests;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import ro.kuberam.maven.xarPlugin.GenerateDescriptorsMojo;

public class GenerateDescriptorsMojoTest extends AbstractMojoTestCase {

	/** {@inheritDoc} */
	protected void setUp() throws Exception {
		// required
		super.setUp();

	}

	/** {@inheritDoc} */
	protected void tearDown() throws Exception {
		// required
		super.tearDown();

	}

	/**
	 * @throws Exception
	 *             if any
	 */
	public void testSomething() throws Exception {
		File pom = getTestFile("src/test/resources/ro/kuberam/maven/xarPlugin/tests/pom.xml");
		assertNotNull(pom);
		//assertTrue(pom.exists());
		
//		GenerateDescriptorsMojo generateDescriptorsMojo = (GenerateDescriptorsMojo) lookupMojo(
//				"touch", pom);
//		assertNotNull(generateDescriptorsMojo);
//		generateDescriptorsMojo.execute();

	}
}
