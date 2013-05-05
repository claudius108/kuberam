package ro.kuberam.maven.xarPlugin.tests;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;

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
		
		//delete the xar
		File existingXar = new File("test.xar");
		existingXar.delete();
		
		makeXar("/home/claudius/workspaces/kuberam/kuberam/kuberam-xar-plugin/src/test/resources/ro/kuberam/maven/xarPlugin/tests/expath-specs", "*.{png,md}");
		
		
		
		
		
		//File sourceDirectory = new File("/home/claudius/workspaces/expath/expath-exist/expath-crypto-exist-lib/target/package-files");

		//ro.kuberam.maven.xarPlugin.GenerateDescriptorsMojo.makeXar(sourceDirectory, "test", "/home/claudius/workspaces/kuberam/kuberam/maven-xar-plugin/src/test/resources/ro/kuberam/maven/xarPlugin/tests/target/xar");

	}
	
	private static void makeXar(String fileSetDirectoryPath, String pattern) {
		
		// start the xar
		ZipOutputStream zos = null;
		try {
			FileOutputStream fos = new FileOutputStream("test.xar");
			zos = new ZipOutputStream(new BufferedOutputStream(fos));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Path directoryPath = Paths.get(fileSetDirectoryPath);

		PathMatcher filter = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

		try (DirectoryStream<Path> ds = Files.newDirectoryStream(directoryPath, pattern)) {
			for (Path path : ds) {
				System.out.println("Evaluating " + path.toAbsolutePath());

				if (filter.matches(path.getFileName())) {
					//resourceList.add(path.toFile());
					System.out.println("Match found Do something!");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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
