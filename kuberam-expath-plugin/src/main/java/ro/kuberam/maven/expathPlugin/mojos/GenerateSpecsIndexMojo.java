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
import java.util.UUID;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-specs-index")
public class GenerateSpecsIndexMojo extends AbstractExpathMojo {

	@Parameter(required = true)
	private File specsDir;

	@Parameter(required = true)
	private String includeSpecIds;
	
	@Parameter(required = true)
	private File outputDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// create the outputDir directory
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
		
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
										element(name("dir"), specsDir.getAbsolutePath()),
										element(name("includes"), element(name("include"), includeSpecIds + ".xml")),
										element(name("stylesheet"), this.getClass().getResource("/ro/kuberam/maven/expathPlugin/xmlspec.xsl")
												.toString()), element(name("outputDir"), specTmpDir)))),
				executionEnvironment(project, session, pluginManager));

		File transformedSpecFile = new File(specTmpDir + File.separator + includeSpecIds + ".xml");
		transformedSpecFile.renameTo(new File(outputDir + File.separator + includeSpecIds + ".html"));
	}

}
