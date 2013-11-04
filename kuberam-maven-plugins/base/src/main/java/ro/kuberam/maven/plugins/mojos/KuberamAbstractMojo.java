package ro.kuberam.maven.plugins.mojos;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;

public class KuberamAbstractMojo extends AbstractMojo {

	@Component
	protected MavenProject project;

	@Component
	protected MavenSession session;

	@Component
	protected BuildPluginManager pluginManager;

	@Component(role = MavenResourcesFiltering.class, hint = "default")
	protected MavenResourcesFiltering mavenResourcesFiltering;

	/**
	 * The character encoding scheme to be applied when filtering resources.
	 */
	@Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}", readonly = true)
	private String encoding;

	/**
	 * Local Maven repository where artifacts are cached during the build
	 * process.
	 */
	@Parameter(defaultValue = "${localRepository}", required = true, readonly = true)
	private ArtifactRepository localRepository;

	/**
	 * The project's remote repositories to use for the resolution of project
	 * dependencies.
	 */
	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
	protected List<RemoteRepository> projectRepos;

	/**
	 * The current repository/network configuration of Maven.
	 */
	@Parameter(defaultValue = "${project.repositorySystemSession}", readonly = true)
	protected RepositorySystemSession repoSession;

	/**
	 * The output directory of the assembled distribution file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	protected File projectBuildDirectory;

	/**
	 * The filename of the assembled distribution file.
	 */
	@Parameter(defaultValue = "${project.build.finalName}", required = true)
	private String finalName;

	/**
	 * Base directory of the project.
	 */
	@Parameter(defaultValue = "${project.basedir}", required = true, readonly = true)
	private File basedir;

	private List<String> filters = Arrays.asList();

	private List<String> defaultNonFilteredFileExtensions = Arrays.asList("jpg", "jpeg", "gif", "bmp", "png");

	public void filterResource(String directory, String include, String targetPath, File outputDirectory) {
		Resource resource = new Resource();
		resource.setDirectory(directory);
		resource.addInclude(include);
		resource.setFiltering(true);
		resource.setTargetPath(targetPath);

		MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution(Collections.singletonList(resource), outputDirectory, project,
				encoding, filters, defaultNonFilteredFileExtensions, session);

		mavenResourcesExecution.setInjectProjectBuildFilters(false);
		mavenResourcesExecution.setOverwrite(true);
		mavenResourcesExecution.setSupportMultiLineFiltering(true);

		try {
			mavenResourcesFiltering.filterResources(mavenResourcesExecution);
		} catch (MavenFilteringException e) {
			e.printStackTrace();
		}

	}

	public void execute() throws MojoExecutionException, MojoFailureException {
	}

	public void setProjectBuildDirectory(File projectBuildDirectory) {
		this.projectBuildDirectory = projectBuildDirectory;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setRepoSession(RepositorySystemSession repoSession) {
		this.repoSession = repoSession;
	}

}
