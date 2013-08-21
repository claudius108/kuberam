package ro.kuberam.maven.expathPlugin.mojos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
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

public class AbstractExpathMojo extends AbstractMojo {

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

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	protected File projectBuildDirectory;

	private List<String> filters = Arrays.asList();

	private List<String> defaultNonFilteredFileExtensions = Arrays.asList("jpg", "jpeg", "gif", "bmp", "png");

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
	}

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

	public static boolean unpack(URL url, File file) {
		if (file.exists())
			return false;
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileOutputStream writer = new FileOutputStream(file);
			url.openConnection();
			InputStream reader = url.openStream();
			byte[] buffer = new byte[153600];
			int bytesRead = 0;
			while ((bytesRead = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, bytesRead);
				buffer = new byte[153600];
			}
			writer.close();
			reader.close();
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Exception occured during unpacking of file '" + file.getName() + "'", e);
		}
	}

	public String getFileBaseName(File file) {
		String specFileBaseName = file.getName();
		return (specFileBaseName.contains(".")) ? specFileBaseName.substring(0, specFileBaseName.lastIndexOf(".")) : specFileBaseName;
	}

	public void createOutputDir(File outputDir) {
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}

}
