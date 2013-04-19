package ro.kuberam.maven.xarPlugin;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.BuildPluginManager;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;

@Mojo(name = "generate-descriptors")
public class GenerateDescriptorsMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Parameter(defaultValue = "${session}", required = true, readonly = true)
	private MavenSession session;

	@Component
	private BuildPluginManager pluginManager;

	@Parameter(required = true)
	private File descriptor;

	@Parameter(defaultValue = "${project.build.directory}/xar", required = true)
	private File outputDirectory;

	public void execute() throws MojoExecutionException {
		
		String outputDir = outputDirectory.getAbsolutePath();

		getLog().info("descriptor.getParent(): " + descriptor.getParent());
		getLog().info("descriptor.getName(): " + descriptor.getName());

		executeMojo(
				plugin(groupId("org.codehaus.mojo"),
						artifactId("xml-maven-plugin"), version("1.0")),
				goal("transform"),
				configuration(
						element(name("forceCreation"), "true"),
						element(name("transformationSets"),
								element(name("transformationSet"),
										element(name("dir"),
												descriptor.getParent()),
										element(name("includes"),
												element(name("include"),
														descriptor.getName())),
										element(name("stylesheet"),
												this.getClass()
														.getResource(
																"generate-descriptors.xsl")
														.toString()),
										element(name("parameters"),
												element(name("parameter"),
														element(name("name"),
																"package-dir"),
														element(name("value"),
																outputDir))),
										element(name("outputDir"), outputDir)))),
				executionEnvironment(project, session, pluginManager));
	}
}
