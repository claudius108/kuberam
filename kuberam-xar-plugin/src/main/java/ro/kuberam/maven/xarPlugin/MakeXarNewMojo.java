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
import java.io.FileReader;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

@Mojo(name = "make-xar-new")
public class MakeXarNewMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}")
	private static MavenProject project;

	@Component
	private MavenSession session;

	@Component
	private BuildPluginManager pluginManager;

	@Component(role = MavenResourcesFiltering.class, hint = "default")
	protected MavenResourcesFiltering mavenResourcesFiltering;

	@Component(role = org.codehaus.plexus.archiver.Archiver.class, hint = "zip")
	private ZipArchiver zipArchiver;

	@Parameter(required = true)
	private File descriptor;

	@Parameter(defaultValue = "${project.build.directory}")
	private File outputDirectory;

	@Parameter(defaultValue = "${project.artifactId}-${project.version}")
	private String finalName;

	@Parameter(property = "project.build.sourceEncoding", defaultValue = "UTF-8")
	private String encoding;

	@Component
	private RepositorySystem repoSystem;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
	private RepositorySystemSession repoSession;

	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
	private static List<RemoteRepository> remoteRepos;

	protected List<String> filters = Arrays.asList();

	private List<String> defaultNonFilteredFileExtensions = Arrays.asList("jpg", "jpeg", "gif", "bmp", "png");
	private final static int BUFFER = 2048;
	private static boolean isEntry = false;
	private static byte data[] = new byte[BUFFER];
	private static String componentsTemplateFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<package xmlns=\"http://exist-db.org/ns/expath-pkg\">${components}</package>";

	public void execute() throws MojoExecutionException, MojoFailureException {

		// test if descriptor file exists
		if (!descriptor.exists()) {
			throw new MojoExecutionException("Global descriptor file does not exist.");
		}

		// set needed variables
		String outputDirectoryPath = outputDirectory.getAbsolutePath();
		String assemblyDescriptorName = descriptor.getName();
		String archiveTmpDirectoryPath = project.getModel().getBuild().getDirectory() + File.separator + "xar-tmp";
		String projectArtifactId = project.getArtifactId();
		String components = "";
		String descriptorsDirectoryPath = outputDirectoryPath + File.separator + "expath-descriptors-"
				+ UUID.randomUUID();

		// Plugin xarPlugin =
		// project.getPlugin("ro.kuberam.maven.plugins:kuberam-xar-plugin");
		// DescriptorConfiguration mainConfig = new
		// DescriptorConfiguration((Xpp3Dom) xarPlugin.getConfiguration());

		// filter the descriptor file
		filterResource(descriptor.getParent(), assemblyDescriptorName, archiveTmpDirectoryPath);

		// get the execution configuration
		FileReader fileReader;
		DescriptorConfiguration executionConfig;
		try {
			fileReader = new FileReader(new File(archiveTmpDirectoryPath + File.separator + assemblyDescriptorName));
			executionConfig = new DescriptorConfiguration(Xpp3DomBuilder.build(fileReader));
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage());
		}

		// extract settings from execution configuration
		List<FileSet> fileSets = executionConfig.getFileSets();
		List<DependencySet> dependencySets = executionConfig.getDependencySets();
		String moduleNamespace = executionConfig.getModuleNamespace();

		// set the zip archiver
		zipArchiver.setCompress(true);
		zipArchiver.setDestFile(new File(outputDirectoryPath + File.separator + finalName + ".xar"));
		zipArchiver.setForced(true);

		// process the maven type dependencies
		for (int i = 0, il = dependencySets.size(); i < il; i++) {
			DependencySet dependencySet = dependencySets.get(i);

			// define the artifact
			Artifact artifactDefinition;
			try {
				artifactDefinition = new DefaultArtifact(dependencySet.groupId + ":" + dependencySet.artifactId + ":"
						+ dependencySet.version);
			} catch (IllegalArgumentException e) {
				throw new MojoFailureException(e.getMessage(), e);
			}

			String artifactIdentifier = artifactDefinition.toString();
			getLog().info("Resolving artifact: " + artifactDefinition);

			// resolve the artifact
			ArtifactRequest request = new ArtifactRequest();
			request.setArtifact(artifactDefinition);
			request.setRepositories(remoteRepos);

			ArtifactResult artifactResult;
			try {
				artifactResult = repoSystem.resolveArtifact(repoSession, request);
			} catch (ArtifactResolutionException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}

			getLog().info("Resolved artifact: " + artifactDefinition);

			Artifact artifact = artifactResult.getArtifact();
			File artifactFile = artifact.getFile();
			String artifactFileAbsolutePath = artifactFile.getAbsolutePath();
			String artifactFileName = artifactFile.getName();
			String dependencySetOutputDirectory = dependencySet.outputDirectory;
			String archiveComponentPath = artifactFileName;
			if (dependencySetOutputDirectory == null || dependencySetOutputDirectory.equals("/")) {
				dependencySetOutputDirectory = "";
			} else {
				archiveComponentPath = dependencySetOutputDirectory + File.separator + artifactFileName;
			}

			// artifact is jar
			if (artifactIdentifier.contains(":jar:")) {
				if (dependencySetOutputDirectory.equals("")) {
					dependencySetOutputDirectory = projectArtifactId;
				}
				components += "<resource><public-uri>" + moduleNamespace + "</public-uri><file>" + archiveComponentPath
						+ "</file></resource>";

			}

			// artifact is anything but jar
			zipArchiver.addFile(artifactFile, archiveComponentPath);

			if (i == 0 && artifactIdentifier.contains(":jar:")) {
				components += "<resource><public-uri>http://exist-db.org/ns/expath-pkg/module-main-class</public-uri><file>"
						+ getMainClass(artifactFileAbsolutePath) + "</file></resource>";
			}
		}

		for (FileSet fileSet : fileSets) {
			// System.out.println("assemblyFileSet.directory: " +
			// fileSet.getDirectory());
		}

		project.getModel().addProperty("components", components);

		// create and filter the components descriptor
		try {
			FileUtils.fileWrite(new File(archiveTmpDirectoryPath + File.separator + "components.xml"), "UTF-8",
					componentsTemplateFileContent);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		filterResource(archiveTmpDirectoryPath, "components.xml", descriptorsDirectoryPath);

		System.out.println("descriptorsDirectoryPath: " + descriptorsDirectoryPath);

		// generate the expath descriptors
		executeMojo(
				plugin(groupId("org.codehaus.mojo"), artifactId("xml-maven-plugin"), version("1.0"),
						dependencies(dependency("net.sf.saxon", "Saxon-HE", "9.4.0.7"))),
				goal("transform"),
				configuration(
						element(name("forceCreation"), "true"),
						element(name("transformationSets"),
								element(name("transformationSet"),
										element(name("dir"), archiveTmpDirectoryPath),
										element(name("includes"), element(name("include"), assemblyDescriptorName)),
										element(name("stylesheet"),
												this.getClass().getResource("generate-descriptors.xsl").toString()),
										element(name("parameters"),
												element(name("parameter"), element(name("name"), "package-dir"),
														element(name("value"), descriptorsDirectoryPath)))))),
				executionEnvironment(project, session, pluginManager));

		// add the expath descriptors
		File descriptorsDirectory = new File(descriptorsDirectoryPath);
		for (String descriptorFileName : descriptorsDirectory.list()) {
			System.out.println("descriptorFileName: " + descriptorsDirectoryPath + File.separator + descriptorFileName);
			zipArchiver.addFile(new File(descriptorsDirectoryPath + File.separator + descriptorFileName), descriptorFileName);
		}

		// make the archive
		// start the xar
		ZipOutputStream zos = null;
		try {
			FileOutputStream fos = new FileOutputStream(outputDirectoryPath + File.separator + finalName + "-old.xar");
			zos = new ZipOutputStream(new BufferedOutputStream(fos));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			zipArchiver.createArchive();
		} catch (ArchiverException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			// add fileSets
			for (final Iterator<FileSet> fsIterator = fileSets.iterator(); fsIterator.hasNext();) {
				final FileSet fileSet = fsIterator.next();
				String fileSetDirectoryLocation = fileSet.getDirectory();
				File fileSetDirectory = new File(fileSetDirectoryLocation);
				if (!fileSetDirectory.exists()) {
					throw new MojoExecutionException("The directory '" + fileSetDirectoryLocation + "' does not exist.");
				}

				String fileSetOutputDirectoryLocation = fileSet.getOutputDirectory();
				fileSetOutputDirectoryLocation = (fileSetOutputDirectoryLocation == null) ? ""
						: fileSetOutputDirectoryLocation + File.separator;

				// TODO: solution for Java 7
				// Path fileSetDirectoryPath =
				// FileSystems.getDefault().getPath(fileSetDirectoryLocation);

				// get includes and filter the file set
				if (!fileSet.getIncludes().isEmpty()) {
					for (final Object include : fileSet.getIncludes()) {
						String pattern = include.toString();
						filterFileSet(pattern, zos, fileSetDirectoryLocation, fileSetOutputDirectoryLocation);

						// TODO: solution for Java 7
						// filterFileSet(pattern, fileSetDirectoryPath, zos,
						// fileSetDirectoryLocation,
						// fileSetOutputDirectoryLocation, fileSetDirectory);
					}
				} else {
					filterFileSet("*", zos, fileSetDirectoryLocation, fileSetOutputDirectoryLocation);

					// TODO: solution for Java 7
					// filterFileSet("**", fileSetDirectoryPath, zos,
					// fileSetDirectoryLocation,
					// fileSetOutputDirectoryLocation, fileSetDirectory);
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

		project.getModel().addProperty("components", "");
	}

	// public static void makeXar(String finalName, String
	// descriptorsDirectoryPath, String outputDirectoryPath)
	// throws MojoExecutionException {
	//
	// // start the xar
	// ZipOutputStream zos = null;
	// try {
	// FileOutputStream fos = new FileOutputStream(outputDirectoryPath +
	// File.separator + finalName + ".xar");
	// zos = new ZipOutputStream(new BufferedOutputStream(fos));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// System.out.println("dependencies.size(): " + dependencies.size());
	// for (int i = 0, il = dependencies.size(); i < il; i++) {
	// Dependency dependency = dependencies.get(i);
	//
	// // define the artifact
	// DefaultArtifact artifactDefinition = new
	// DefaultArtifact(dependency.getGroupId() + ":"
	// + dependency.getArtifactId() + ":" + dependency.getVersion());
	// String artifactIdentifier = artifactDefinition.toString();
	//
	// // resolve the artifact
	// ArtifactRequest request = new ArtifactRequest();
	// request.setArtifact(artifactDefinition);
	// request.setRepositories(remoteRepos);
	// //
	// ArtifactResult artifactResult;
	// try {
	// artifactResult = repoSystem.resolveArtifact(repoSession, request);
	// } catch (ArtifactResolutionException e) {
	// throw new MojoExecutionException(e.getMessage(), e);
	// }
	//
	// // Artifact artifact = artifactResult.getArtifact();
	// // File artifactFile = artifact.getFile();
	// // String artifactFileAbsolutePath = artifactFile.getAbsolutePath();
	// // String artifactFileName = artifactFile.getName();
	// //
	// // // if artifact is jar, then deploy it in xar, in a dedicated
	// // folder
	// //
	// // // if artifact is zip, unzip it to root of xar
	// // if (artifactIdentifier.contains(":jar:")) {
	// // xarJars += "<jar xmlns=\"http://exist-db.org/ns/expath-pkg\">" +
	// // artifactFileName + "</jar>";
	// //
	// // }
	// //
	// // if (i == 0 && artifactIdentifier.contains(":jar:")) {
	// // project.getModel().addProperty("xar-main-class",
	// // getMainClass(artifactFileAbsolutePath));
	// // }
	// }
	//
	// project.getModel().addProperty("xar-jars", xarJars);
	//
	// try {
	// // add the expath descriptors
	// File descriptorsDirectory = new File(descriptorsDirectoryPath);
	// for (String descriptorFileName : descriptorsDirectory.list()) {
	// addFileToXar(zos, new FileInputStream(descriptorsDirectoryPath +
	// File.separator + descriptorFileName),
	// descriptorFileName);
	// }
	//
	// // add fileSets
	// for (final Iterator<FileSet> fsIterator = fileSets.iterator();
	// fsIterator.hasNext();) {
	// final FileSet fileSet = fsIterator.next();
	// String fileSetDirectoryLocation = fileSet.getDirectory();
	// File fileSetDirectory = new File(fileSetDirectoryLocation);
	// if (!fileSetDirectory.exists()) {
	// throw new MojoExecutionException("The directory '" +
	// fileSetDirectoryLocation + "' does not exist.");
	// }
	//
	// String fileSetOutputDirectoryLocation = fileSet.getOutputDirectory();
	// fileSetOutputDirectoryLocation = (fileSetOutputDirectoryLocation == null)
	// ? ""
	// : fileSetOutputDirectoryLocation + File.separator;
	//
	// // TODO: solution for Java 7
	// // Path fileSetDirectoryPath =
	// // FileSystems.getDefault().getPath(fileSetDirectoryLocation);
	//
	// // get includes and filter the file set
	// if (!fileSet.getIncludes().isEmpty()) {
	// for (final Object include : fileSet.getIncludes()) {
	// String pattern = include.toString();
	// filterFileSet(pattern, zos, fileSetDirectoryLocation,
	// fileSetOutputDirectoryLocation);
	//
	// // TODO: solution for Java 7
	// // filterFileSet(pattern, fileSetDirectoryPath, zos,
	// // fileSetDirectoryLocation,
	// // fileSetOutputDirectoryLocation, fileSetDirectory);
	// }
	// } else {
	// filterFileSet("*", zos, fileSetDirectoryLocation,
	// fileSetOutputDirectoryLocation);
	//
	// // TODO: solution for Java 7
	// // filterFileSet("**", fileSetDirectoryPath, zos,
	// // fileSetDirectoryLocation,
	// // fileSetOutputDirectoryLocation, fileSetDirectory);
	// }
	//
	// }
	// // close the xar
	// if (isEntry) {
	// zos.close();
	// } else {
	// zos = null;
	// System.out.println("No Entry Found in Zip");
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	/**
	 * Filters a file set.
	 * 
	 * @throws IOException
	 */

	private static void filterFileSet(String pattern, ZipOutputStream zos, String fileSetDirectoryLocation,
			String fileSetOutputDirectoryLocation) throws Exception {

		FileSet inputFileSet = new FileSet();
		inputFileSet.setDirectory(fileSetDirectoryLocation);
		inputFileSet.addInclude(pattern);

		FileSetManager fsm1 = new FileSetManager();
		String[] fileSets = fsm1.getIncludedDirectories(inputFileSet);

		for (String fileSet : fileSets) {
			if (fileSet.equals("")) {
				continue;
			}
			addFileSetToXar(zos, fileSetDirectoryLocation + File.separator + fileSet, fileSet,
					fileSetOutputDirectoryLocation);
		}

		// fsm.delete(inputFileSet);

		FileSetManager fsm2 = new FileSetManager();
		String[] files = fsm2.getIncludedFiles(inputFileSet);

		for (String file : files) {
			String resourceLocation = fileSetDirectoryLocation + File.separator + file;
			addFileToXar(zos, new FileInputStream(resourceLocation), fileSetOutputDirectoryLocation + file);
		}
	}

	// TODO: this is the solution for Java 7
	// private static void filterFileSetJava7(String pattern, Path
	// fileSetDirectoryPath, ZipOutputStream zos,
	// String fileSetDirectoryLocation, String fileSetOutputDirectoryLocation,
	// File fileSetDirectory) {
	//
	// DirectoryStream<Path> ds = null;
	// try {
	// ds = Files.newDirectoryStream(fileSetDirectoryPath, pattern);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// try {
	// for (Path path : ds) {
	// String resourceLocation = path.toAbsolutePath().toString();
	// if (path.toFile().isDirectory()) {
	// addFileSetToXar(zos, fileSetDirectoryLocation,
	// fileSetOutputDirectoryLocation);
	// } else {
	// addFileToXar(zos, new FileInputStream(resourceLocation),
	// fileSetOutputDirectoryLocation + path.getFileName());
	// }
	// }
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// } finally {
	// try {
	// ds.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	/**
	 * Adds a file set to a xar package.
	 * 
	 * @throws IOException
	 */
	private static void addFileSetToXar(ZipOutputStream zos, String fileSetDirectoryPath, String fileSetName,
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
			String archiveEntryDirectory = fileSetOutputDirectoryPath + fileSetName + File.separator + directoryName;

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

	private static String getMainClass(String firstDependencyAbsolutePath) {
		URL u;
		JarURLConnection uc;
		Attributes attr = null;
		try {
			u = new URL("jar", "", "file://" + firstDependencyAbsolutePath + "!/");
			uc = (JarURLConnection) u.openConnection();
			attr = uc.getMainAttributes();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return attr.getValue(Attributes.Name.MAIN_CLASS);
	}

	private void filterResource(String directory, String include, String targetPath) {
		Resource resource = new Resource();
		resource.setDirectory(directory);
		resource.addInclude(include);
		resource.setFiltering(true);
		resource.setTargetPath(targetPath);

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

	}

}