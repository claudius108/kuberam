package ro.kuberam.maven.xarPlugin.tests;

import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.junit.Test;

public class GenerateDescriptorsMojoTest {

	// protected void setUp() throws Exception {
	// super.setUp();
	// }

	public void generateDescriptorsMojoTest() throws Exception {

		// GenerateDescriptorsMojoTest mojo =
		// getGenerateDescriptorsMojo("src/test/resources/ro/kuberam/maven/xarPlugin/tests/pom.xml");

		// mojo.execute();
	}

	@Test
	public void makeXarTest() throws Exception {

		FileSet fileSet = new FileSet();
		fileSet.setDirectory("/home/claudius");
		fileSet.addInclude("*");

		FileSetManager fsm = new FileSetManager();
		String[] files = fsm.getIncludedDirectories(fileSet);
		
		for (String file : files) {
			System.out.println(file);
		}

		// File sourceDirectory = new
		// File("/home/claudius/workspaces/expath/expath-exist/expath-crypto-exist-lib/target/package-files");

		// ro.kuberam.maven.xarPlugin.GenerateDescriptorsMojo.makeXar(sourceDirectory,
		// "test",
		// "/home/claudius/workspaces/kuberam/kuberam/maven-xar-plugin/src/test/resources/ro/kuberam/maven/xarPlugin/tests/target/xar");

	}

	// private GenerateDescriptorsMojoTest getGenerateDescriptorsMojo(String
	// pomXml) throws Exception {
	// File testPom = new File(getBasedir(), pomXml);
	//
	// GenerateDescriptorsMojoTest mojo = (GenerateDescriptorsMojoTest)
	// lookupMojo("generate-descriptors", testPom);
	//
	// assertNotNull(mojo);
	//
	// return mojo;
	// }

}
