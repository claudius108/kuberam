package ro.kuberam.maven.expathPlugin.mojos;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependencies;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependency;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-lib-basics")
public class GenerateLibBasicsMojo extends AbstractExpathMojo {

	@Parameter(required = true)
	private String specId;

	@Parameter(required = true)
	private File specDir;

	@Parameter(required = true)
	private File libDir;

	@Parameter(required = true)
	private String javaPackageName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		libDir = new File(libDir + File.separator + specId);

		// create the lib directory
		if (!libDir.exists()) {
			libDir.mkdir();
		}

		String libDirPath = libDir.getAbsolutePath();
		String libVersion = project.getVersion();

		// filter expath-lib-pom-template.xml
		unpack(GenerateLibBasicsMojo.class.getResource("/ro/kuberam/maven/expathPlugin/expath-lib-pom-template.xml"), new File(projectBuildDirectory,
				"expath-lib-pom-template.xml"));
		filterResource(projectBuildDirectory.getAbsolutePath(), "expath-lib-pom-template.xml", libDirPath, libDir);

		// generate java classes
		executeMojo(
				plugin(groupId("org.codehaus.mojo"), artifactId("xml-maven-plugin"), version("1.0"),
						dependencies(dependency("net.sf.saxon", "Saxon-HE", "9.4.0.7"))),
				goal("transform"),
				configuration(
						element(name("forceCreation"), "true"),
						element(name("transformationSets"),
								element(name("transformationSet"),
										element(name("dir"), specDir.getAbsolutePath()),
										element(name("includes"), element(name("include"), specId + ".xml")),
										element(name("stylesheet"),
												this.getClass().getResource("/ro/kuberam/maven/expathPlugin/generate-java-classes.xsl").toString()),
										element(name("parameters"),
												element(name("parameter"), element(name("name"), "javaPackageName"),
														element(name("value"), javaPackageName)),
												element(name("parameter"), element(name("name"), "specId"), element(name("value"), specId)),
												element(name("parameter"), element(name("name"), "libDirPath"), element(name("value"), libDirPath)),
												element(name("parameter"), element(name("name"), "libVersion"), element(name("value"), libVersion)))))),
				executionEnvironment(project, session, pluginManager));

		System.out.println("URL: " + GenerateLibBasicsMojo.class.getResource("expath-lib-pom-template.xml"));

	}

}
