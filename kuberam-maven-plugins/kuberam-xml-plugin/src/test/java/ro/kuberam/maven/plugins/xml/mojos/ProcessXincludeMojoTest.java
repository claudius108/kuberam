package ro.kuberam.maven.plugins.xml.mojos;

import java.io.File;

import org.apache.maven.project.MavenProject;

import ro.kuberam.maven.plugins.utils.KuberamAbstractMojoTestBase;

public class ProcessXincludeMojoTest extends KuberamAbstractMojoTestBase {

	public void testValidationSuccess() throws Exception {
		final ProcessXincludeMojo mojo = this.mojo();
		
		setVariableValueToObject(mojo, "inputFile", new File(baseDir + "src/test/resources/ro/kuberam/maven/plugins/xml/mojos/process-xinclude/document.xml"));
		setVariableValueToObject(mojo, "outputDir", new File(projectBuildDirectory + "process-xinclude"));
		mojo.execute();
	}

	// public void testValidationFailedWithMojoFailure() throws Exception {
	// File changesXml = new File(getBasedir(),
	// "/src/test/unit/non-valid-changes.xml");
	// setVariableValueToObject(mojo, "xmlPath", changesXml);
	// setVariableValueToObject(mojo, "changesXsdVersion", "1.0.0");
	// setVariableValueToObject(mojo, "failOnError", Boolean.TRUE);
	// try {
	// mojo.execute();
	// fail(" a MojoExecutionException should occur here changes file is not valid and failOnError is true ");
	// } catch (MojoExecutionException e) {
	// // we except exception here
	// }
	// }
	//
	// public void testValidationFailedWithNoMojoFailure() throws Exception {
	// File changesXml = new File(getBasedir(),
	// "/src/test/unit/non-valid-changes.xml");
	// setVariableValueToObject(mojo, "xmlPath", changesXml);
	// setVariableValueToObject(mojo, "changesXsdVersion", "1.0.0");
	// setVariableValueToObject(mojo, "failOnError", Boolean.FALSE);
	// mojo.execute();
	//
	// }

	private ProcessXincludeMojo mojo() throws Exception {
		final ProcessXincludeMojo mojo = new ProcessXincludeMojo();

		mojo.setProjectBuildDirectory(new File(projectBuildDirectory));
		mojo.setProject(new MavenProject());
		mojo.setRepoSession(newSession(newRepositorySystem()));

		return mojo;
	}
}
