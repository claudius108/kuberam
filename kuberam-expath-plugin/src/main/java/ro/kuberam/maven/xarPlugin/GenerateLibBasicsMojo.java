package ro.kuberam.maven.xarPlugin;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import ro.kuberam.maven.xarPlugin.mojos.AbstractExpathMojo;

@Mojo(name = "generate-lib-basics")
public class GenerateLibBasicsMojo extends AbstractExpathMojo {

	@Parameter(required = true)
	private File libDirPath;

	@Parameter(defaultValue = "${project.artifactId}")
	private String projectArtifactId;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		libDirPath = new File(libDirPath + File.separator + projectArtifactId);

		// create the lib directory
		if (!libDirPath.exists()) {
			libDirPath.mkdir();
		}

		// filter expath-lib-pom-template.xml
		unpack(GenerateLibBasicsMojo.class.getResource("expath-lib-pom-template.xml"), new File(projectBuildDirectory, "expath-lib-pom-template.xml"));
		filterResource(projectBuildDirectory.getAbsolutePath(), "expath-lib-pom-template.xml", libDirPath.getAbsolutePath(), libDirPath);
		

		System.out.println("URL: " + GenerateLibBasicsMojo.class.getResource("expath-lib-pom-template.xml"));
		
//		FilterResource.filter(archiveTmpDirectoryPath, "components.xml", descriptorsDirectoryPath, project, session,
//				mavenResourcesFiltering, outputDirectory, encoding);

	}

}
