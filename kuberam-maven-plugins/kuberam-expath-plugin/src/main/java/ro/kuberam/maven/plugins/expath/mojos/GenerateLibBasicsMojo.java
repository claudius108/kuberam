package ro.kuberam.maven.plugins.expath.mojos;

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

import ro.kuberam.maven.plugins.mojos.KuberamAbstractMojo;
import ro.kuberam.maven.plugins.utils.KuberamMojoUtils;

/**
 * Generates the basic files needed for the library implementing the EXPath
 * module. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius
 *         Teodorescu</a>
 * 
 */

@Mojo(name = "generate-lib-basics")
public class GenerateLibBasicsMojo extends KuberamAbstractMojo {

	/**
	 * Specification file.
	 * 
	 * @parameter
	 * @since 0.3
	 * 
	 */
	@Parameter(required = true)
	private File specFile;

	/**
	 * Library's dir.
	 * 
	 * @parameter
	 * @since 0.2
	 * 
	 */
	@Parameter(required = true)
	private File libDir;

	/**
	 * Library's java package name.
	 * 
	 * @parameter
	 * @since 0.2
	 * 
	 */
	@Parameter(required = true)
	private String javaPackageName;

	/**
	 * Library's version.
	 * 
	 * @parameter
	 * @since 0.2
	 * 
	 */
	@Parameter(required = true)
	private String libVersion;

	/**
	 * Library's URL.
	 * 
	 * @parameter
	 * @since 0.2
	 * 
	 */
	@Parameter(defaultValue = "${project.url}")
	private String libUrl;

	/**
	 * Library's artifactId for generating the pom file needed for library.
	 * 
	 * @parameter
	 * @since 0.2
	 * 
	 */
	@Parameter(required = true)
	private String libArtifactId;

	/**
	 * Library's name.
	 * 
	 * @parameter
	 * @since 0.2
	 * 
	 */
	@Parameter(required = true)
	private String libName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		KuberamMojoUtils.createDir(libDir);

		String specFileBaseName = KuberamMojoUtils.getFileBaseName(specFile);
		
		// generate java classes
		executeMojo(
				plugin(groupId("org.codehaus.mojo"), artifactId("xml-maven-plugin"), version("1.0"),
						dependencies(dependency("net.sf.saxon", "Saxon-HE", "9.4.0.7"))),
				goal("transform"),
				configuration(
						element(name("forceCreation"), "true"),
						element(name("transformationSets"),
								element(name("transformationSet"),
										element(name("dir"), specFile.getParentFile().getAbsolutePath()),
										element(name("includes"), element(name("include"), specFileBaseName + ".xml")),
										element(name("stylesheet"),
												this.getClass().getResource("/ro/kuberam/maven/plugins/expath/generate-lib-basics.xsl").toString()),
										element(name("parameters"),
												element(name("parameter"), element(name("name"), "javaPackageName"),
														element(name("value"), javaPackageName)),
												element(name("parameter"), element(name("name"), "libDirPath"), element(name("value"), libDir.getAbsolutePath())),
												element(name("parameter"), element(name("name"), "libUrl"), element(name("value"), libUrl)),
												element(name("parameter"), element(name("name"), "libArtifactId"), element(name("value"), libArtifactId)),
												element(name("parameter"), element(name("name"), "libName"), element(name("value"), libName)),
												element(name("parameter"), element(name("name"), "libVersion"), element(name("value"), libVersion)))))),
				executionEnvironment(project, session, pluginManager));
	}

}
