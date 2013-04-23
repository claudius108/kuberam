package ro.kuberam.maven.xarPlugin;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependencies;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

@Mojo(name = "generate-descriptors")
@Execute(goal = "generate-descriptors")
public class GenerateDescriptorsMojo extends AbstractMojo {

	@Component
	private MavenProject project;

	@Component
	private MavenSession session;

	@Component
	private BuildPluginManager pluginManager;

	@Component(role = MavenResourcesFiltering.class, hint = "default")
	protected MavenResourcesFiltering mavenResourcesFiltering;

	@Parameter(required = true)
	private File descriptor;

	@Parameter(defaultValue = "${project.build.directory}/xar", required = true)
	private File outputDirectory;

	@Parameter(property = "project.build.sourceEncoding", defaultValue = "UTF-8")
	private String encoding;

	protected List<String> filters = Arrays.asList();

	private List<String> defaultNonFilteredFileExtensions = Arrays.asList("jpg", "jpeg", "gif", "bmp", "png");

	public void execute() throws MojoExecutionException {

		String outputDir = outputDirectory.getAbsolutePath();
		String projectBuildDirectory = project.getModel().getBuild().getDirectory();
		String descriptorName = descriptor.getName();		

		Resource resource = new Resource();
		resource.setDirectory(descriptor.getParent());
		resource.addInclude(descriptorName);
		resource.setFiltering(true);
		resource.setTargetPath(projectBuildDirectory);
		List<Resource> listResources = new ArrayList<Resource>();
		listResources.add(resource);

		MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution(
				Collections.singletonList(resource), outputDirectory, project, encoding, filters,
				defaultNonFilteredFileExtensions, session);

		mavenResourcesExecution.setInjectProjectBuildFilters(false);
		mavenResourcesExecution.setOverwrite(true);
		mavenResourcesExecution.setSupportMultiLineFiltering(true);

		try {
			mavenResourcesFiltering.filterResources(mavenResourcesExecution);
		} catch (MavenFilteringException e) {
			e.printStackTrace();
		}

		executeMojo(
				plugin(groupId("org.codehaus.mojo"), artifactId("xml-maven-plugin"), version("1.0"),
						dependencies(dependency("net.sf.saxon", "Saxon-HE", "9.4.0.7"))),
				goal("transform"),
				configuration(
						element(name("forceCreation"), "true"),
						element(name("transformationSets"),
								element(name("transformationSet"),
										element(name("dir"), projectBuildDirectory),
										element(name("includes"), element(name("include"), descriptorName)),
										element(name("stylesheet"),
												this.getClass().getResource("generate-descriptors.xsl").toString()),
										element(name("parameters"),
												element(name("parameter"), element(name("name"), "package-dir"),
														element(name("value"), outputDir)))))),
				executionEnvironment(project, session, pluginManager));
	}
}
