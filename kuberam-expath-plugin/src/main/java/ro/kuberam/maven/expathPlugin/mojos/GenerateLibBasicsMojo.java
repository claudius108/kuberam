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

/**
 * Generates the basic files needed for the library implementing the EXPath module. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius Teodorescu</a>
 * 
 */

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
	
	@Parameter(required = true)
	private String libVersion;
	
	@Parameter(defaultValue = "${project.url}")
	private String libUrl;
	
	@Parameter(required = true)
	private String libId;
	
	@Parameter(required = true)
	private String libName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// create the lib directory
		if (!libDir.exists()) {
			libDir.mkdir();
		}

		String libDirPath = libDir.getAbsolutePath();

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
												this.getClass().getResource("/ro/kuberam/maven/expathPlugin/generate-lib-basics.xsl").toString()),
										element(name("parameters"),
												element(name("parameter"), element(name("name"), "javaPackageName"),
														element(name("value"), javaPackageName)),
												element(name("parameter"), element(name("name"), "specId"), element(name("value"), specId)),
												element(name("parameter"), element(name("name"), "libDirPath"), element(name("value"), libDirPath)),
												element(name("parameter"), element(name("name"), "libUrl"), element(name("value"), libUrl)),
												element(name("parameter"), element(name("name"), "libId"), element(name("value"), libId)),
												element(name("parameter"), element(name("name"), "libName"), element(name("value"), libName)),
												element(name("parameter"), element(name("name"), "libVersion"), element(name("value"), libVersion)))))),
				executionEnvironment(project, session, pluginManager));
	}

}
