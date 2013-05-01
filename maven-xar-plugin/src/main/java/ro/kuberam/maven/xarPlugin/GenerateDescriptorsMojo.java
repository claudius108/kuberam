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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

//@Mojo(name = "generate-descriptors")
@Mojo(name = "make-xar")
//@Execute(goal = "generate-descriptors")
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
	private static File sourceDirectory;

	@Parameter(required = true)
	private File descriptor;

	@Parameter(defaultValue = "${project.build.directory}")
	private File outputDirectory;

	@Parameter(defaultValue = "${project.artifactId}-${project.version}")
	private String finalName;

	@Parameter(property = "project.build.sourceEncoding", defaultValue = "UTF-8")
	private String encoding;

	protected List<String> filters = Arrays.asList();

	private List<String> defaultNonFilteredFileExtensions = Arrays.asList("jpg", "jpeg", "gif", "bmp", "png");
	
	private final static int BUFFER = 2048;
	private static boolean isEntry = false;
	private static byte data[] = new byte[BUFFER];
	private static String[] descriptorFileNames = {"controller.xql", "cxan.xml", "exist.xml", "expath-pkg.xml", "repo.xml"};

	public void execute() throws MojoExecutionException {

		// filter the assembly file
		String outputDirectoryPath = outputDirectory.getAbsolutePath();
		String descriptorName = descriptor.getName();
		String projectBuildDirectory = project.getModel().getBuild().getDirectory();
		String descriptorsDirectoryPath = projectBuildDirectory + File.separator + "expath-descriptors";		

		Resource resource = new Resource();
		resource.setDirectory(descriptor.getParent());
		resource.addInclude(descriptorName);
		resource.setFiltering(true);
		resource.setTargetPath(projectBuildDirectory);

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

		// generate the descriptors
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
														element(name("value"), descriptorsDirectoryPath)))))),
				executionEnvironment(project, session, pluginManager));

		// make the archive
		makeXar(sourceDirectory, finalName, outputDirectoryPath, descriptorsDirectoryPath, outputDirectoryPath);
	}

	public static void makeXar(File sourceDirectory, String finalName, String outputDir, String descriptorsDirectoryPath, String outputDirectoryPath) {
		ArrayList<String> directoryList = new ArrayList<String>();
		String sourceDirectoryPath = sourceDirectory.getAbsolutePath() + File.separator;

		System.out.println("sourceDirectoryPath: " + sourceDirectoryPath);

		if (sourceDirectory.exists()) {
			try {
				FileOutputStream fos = new FileOutputStream(outputDirectoryPath + File.separator + finalName + ".xar");
				ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos));

				if (sourceDirectory.isDirectory()) {
					// add the descriptors
					for (String descriptorFileName : descriptorFileNames) {
						addFileToXar(new FileInputStream(descriptorsDirectoryPath + File.separator + descriptorFileName), zos, descriptorFileName);
					}
					
					// This is Directory
					do {
						String directoryName = "";
						if (directoryList.size() > 0) {
							directoryName = directoryList.get(0);
							System.out.println("Directory Name At 0: " + directoryName);
						}
						String fullPath = sourceDirectoryPath + directoryName;
						File fileList = null;
						if (directoryList.size() == 0) {
							// Main path (Root Directory)
							fileList = sourceDirectory;
						} else {
							// Child Directory
							fileList = new File(fullPath);
						}
						String[] filesName = fileList.list();

						int totalFiles = filesName.length;
						for (int i = 0; i < totalFiles; i++) {
							String name = filesName[i];
							File filesOrDir = new File(fullPath + name);
							if (filesOrDir.isDirectory()) {
								System.out.println("New Directory Entry: " + directoryName + name + "/");
								ZipEntry entry = new ZipEntry(directoryName + name + "/");
								zos.putNextEntry(entry);
								isEntry = true;
								directoryList.add(directoryName + name + "/");
							} else {
								System.out.println("New File Entry: " + directoryName + name);
								FileInputStream fileInputStream = new FileInputStream(filesOrDir);
								
								addFileToXar(fileInputStream, zos, directoryName + name);
							}
						}
						if (directoryList.size() > 0 && directoryName.trim().length() > 0) {
							System.out.println("Directory removed: " + directoryName);
							directoryList.remove(0);
						}

					} while (directoryList.size() > 0);
				} else {
					// This is File
					// Zip this file
					System.out.println("Zip this file: " + sourceDirectory.getPath());
					FileInputStream fis = new FileInputStream(sourceDirectory);					
					addFileToXar(fis, zos, sourceDirectory.getName());
				}

				// CHECK IS THERE ANY ENTRY IN ZIP ? ----START
				if (isEntry) {
					zos.close();
				} else {
					zos = null;
					System.out.println("No Entry Found in Zip");
				}
				// CHECK IS THERE ANY ENTRY IN ZIP ? ----START
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("File or Directory not found");
		}
	}

	/**
	 * Adds a file to a xar package.
	 * 
	 * @throws IOException
	 */
	private static void addFileToXar(FileInputStream fis, ZipOutputStream zos, String entryFullPath) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(fis, BUFFER);
		ZipEntry entry = new ZipEntry(entryFullPath);
		zos.putNextEntry(entry);
		isEntry = true;
		int size = -1;
		while ((size = bis.read(data, 0, BUFFER)) != -1) {
			zos.write(data, 0, size);
		}
		bis.close();
	}
}
