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
import java.util.UUID;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Transforms an EXPath specification to HTML format. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius
 *         Teodorescu</a>
 * 
 */

@Mojo(name = "transform-spec-to-html")
public class TransformSpecToHtmlMojo extends KuberamAbstractMojo {

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
	 * Output directory.
	 * 
	 * @parameter
	 * @since 0.2
	 * 
	 */
	@Parameter(required = true)
	private File outputDir;

	/**
	 * Google Analytics account id, in case one needs to track the page.
	 * 
	 * @parameter
	 * @since 0.3
	 * 
	 */
	@Parameter(defaultValue = "")
	private String googleAnalyticsAccountId;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		createOutputDir(outputDir);

		String specFileBaseName = getFileBaseName(specFile);

		String specTmpDir = projectBuildDirectory.getAbsolutePath() + File.separator + "spec-tmp-" + UUID.randomUUID();

		// transform the spec
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
												this.getClass().getResource("/ro/kuberam/maven/expathPlugin/xmlspec/transform-spec.xsl").toString()),
										element(name("parameters"),
												element(name("parameter"), element(name("name"), "googleAnalyticsAccountId"),
														element(name("value"), googleAnalyticsAccountId))), element(name("outputDir"), specTmpDir)))),
				executionEnvironment(project, session, pluginManager));

		File transformedSpecFile = new File(specTmpDir + File.separator + specFileBaseName + ".xml");
		transformedSpecFile.renameTo(new File(outputDir + File.separator + specFileBaseName + ".html"));
	}

}
