package ro.kuberam.maven.xarPlugin;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

//@Mojo(name = "generate-descriptors")
@Mojo(name = "make-xar")
// @Execute(goal = "generate-descriptors")
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
	private static List<FileSet> fileSets = new ArrayList<FileSet>();

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

	public void execute() throws MojoExecutionException {

		// test if descriptor file exists
		if (!descriptor.exists()) {
			throw new MojoExecutionException("Descriptor file does not exist.");
		}

		String outputDirectoryPath = outputDirectory.getAbsolutePath();
		String descriptorName = descriptor.getName();
		String projectBuildDirectory = project.getModel().getBuild().getDirectory();
		String descriptorsDirectoryPath = projectBuildDirectory + File.separator + "expath-descriptors-"
				+ UUID.randomUUID();

		// filter the assembly file
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
		makeXar(finalName, descriptorsDirectoryPath, outputDirectoryPath);
	}

	public static void makeXar(String finalName, String descriptorsDirectoryPath, String outputDirectoryPath) {

		// start the xar
		ZipOutputStream zos = null;
		try {
			FileOutputStream fos = new FileOutputStream(outputDirectoryPath + File.separator + finalName + ".xar");
			zos = new ZipOutputStream(new BufferedOutputStream(fos));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// add the descriptors
			File descriptorsDirectory = new File(descriptorsDirectoryPath);
			for (String descriptorFileName : descriptorsDirectory.list()) {
				addFileToXar(zos, new FileInputStream(descriptorsDirectoryPath + File.separator + descriptorFileName),
						descriptorFileName);
			}

			// add fileSets
			for (final Iterator<FileSet> fsIterator = fileSets.iterator(); fsIterator.hasNext();) {
				final FileSet fileSet = fsIterator.next();
				String fileSetDirectoryLocation = fileSet.getDirectory();
				String fileSetOutputDirectoryLocation = fileSet.getOutputDirectory();
				fileSetOutputDirectoryLocation = (fileSetOutputDirectoryLocation == null) ? ""
						: fileSetOutputDirectoryLocation + File.separator;
				File fileSetDirectory = new File(fileSetDirectoryLocation);
				Path fileSetDirectoryPath = FileSystems.getDefault().getPath(fileSetDirectoryLocation);

				// get includes and filter the file set
				if (!fileSet.getIncludes().isEmpty()) {
					for (final Object include : fileSet.getIncludes()) {
						String pattern = include.toString();
						System.out.println("pattern: " + pattern);
						filterFileSet(pattern, fileSetDirectoryPath, zos, fileSetDirectoryLocation,
								fileSetOutputDirectoryLocation, fileSetDirectory);
					}
				} else {
					filterFileSet("**", fileSetDirectoryPath, zos, fileSetDirectoryLocation,
							fileSetOutputDirectoryLocation, fileSetDirectory);
				}

			}
			// close the xar
			if (isEntry) {
				zos.close();
			} else {
				zos = null;
				System.out.println("No Entry Found in Zip");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Filters a file set.
	 * 
	 * @throws IOException
	 */

	private static void filterFileSet(String pattern, Path fileSetDirectoryPath, ZipOutputStream zos,
			String fileSetDirectoryLocation, String fileSetOutputDirectoryLocation, File fileSetDirectory) {

		PathMatcher filter = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

		DirectoryStream<Path> ds = null;
		try {
			ds = Files.newDirectoryStream(fileSetDirectoryPath, pattern);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			for (Path path : ds) {
				String resourceLocation = path.toAbsolutePath().toString();
				if (path.toFile().isDirectory()) {
					addFileSetToXar(zos, fileSetDirectoryLocation, fileSetOutputDirectoryLocation);
				} else {
					addFileToXar(zos, new FileInputStream(resourceLocation), fileSetOutputDirectoryLocation
							+ path.getFileName());
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				ds.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a file set to a xar package.
	 * 
	 * @throws IOException
	 */
	private static void addFileSetToXar(ZipOutputStream zos, String fileSetDirectoryPath,
			String fileSetOutputDirectoryPath) throws IOException {
		ArrayList<String> directoryList = new ArrayList<String>();

		isEntry = false;

		do {
			String directoryName = "";
			if (directoryList.size() > 0) {
				directoryName = directoryList.get(0);
				System.out.println("Directory Name At 0: " + directoryName);
			}
			String fullPath = fileSetDirectoryPath + File.separator + directoryName;
			String archiveEntryDirectory = fileSetOutputDirectoryPath + directoryName;

			File fileList = null;
			if (directoryList.size() == 0) {
				fileList = new File(fileSetDirectoryPath);
			} else {
				fileList = new File(fullPath);
			}

			String[] filesName = fileList.list();
			int totalFiles = filesName.length;

			for (int i = 0; i < totalFiles; i++) {
				String name = filesName[i];
				File filesOrDir = new File(fullPath + name);
				if (filesOrDir.isDirectory()) {
					System.out.println("New Directory Entry: " + archiveEntryDirectory + name + "/");
					ZipEntry entry = new ZipEntry(archiveEntryDirectory + name + "/");
					zos.putNextEntry(entry);
					isEntry = true;
					directoryList.add(directoryName + name + "/");
				} else {
					System.out.println("New File Entry: " + archiveEntryDirectory + name);
					FileInputStream fileInputStream = new FileInputStream(filesOrDir);
					addFileToXar(zos, fileInputStream, archiveEntryDirectory + name);
				}
			}
			if (directoryList.size() > 0 && directoryName.trim().length() > 0) {
				System.out.println("Directory removed: " + directoryName);
				directoryList.remove(0);
			}

		} while (directoryList.size() > 0);
	}

	/**
	 * Adds a file to a xar package.
	 * 
	 * @throws IOException
	 */
	private static void addFileToXar(ZipOutputStream zos, FileInputStream fis, String entryFullPath) throws IOException {
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
