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
import java.io.FileReader;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.jar.Attributes;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import ro.kuberam.maven.plugins.expath.DefaultFileSet;
import ro.kuberam.maven.plugins.expath.DependencySet;
import ro.kuberam.maven.plugins.expath.DescriptorConfiguration;
import ro.kuberam.maven.plugins.mojos.KuberamAbstractMojo;

/**
 * Assembles a package. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius
 *         Teodorescu</a>
 * 
 */

@Mojo(name = "make-xar")
public class MakeXarMojo extends KuberamAbstractMojo {

	@Component(role = org.codehaus.plexus.archiver.Archiver.class, hint = "zip")
	private ZipArchiver zipArchiver;

	@Parameter(required = true)
	private File descriptor;

	@Parameter(defaultValue = "${project.build.directory}")
	private File outputDirectory;

	@Component
	private RepositorySystem repoSystem;

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
		String components = "";
		String descriptorsDirectoryPath = outputDirectoryPath + File.separator + "expath-descriptors-" + UUID.randomUUID();

		// Plugin xarPlugin =
		// project.getPlugin("ro.kuberam.maven.plugins:kuberam-xar-plugin");
		// DescriptorConfiguration mainConfig = new
		// DescriptorConfiguration((Xpp3Dom) xarPlugin.getConfiguration());

		// filter the descriptor file
		filterResource(descriptor.getParent(), assemblyDescriptorName, archiveTmpDirectoryPath, outputDirectory);

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
		List<DefaultFileSet> fileSets = executionConfig.getFileSets();
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
			Artifact artifactReference;
			try {
				artifactReference = new DefaultArtifact(dependencySet.groupId + ":" + dependencySet.artifactId + ":" + dependencySet.version);
			} catch (IllegalArgumentException e) {
				throw new MojoFailureException(e.getMessage(), e);
			}

			String artifactIdentifier = artifactReference.toString();
			getLog().info("Resolving artifact: " + artifactReference);

			// resolve the artifact
			ArtifactRequest request = new ArtifactRequest();
			request.setArtifact(artifactReference);
			request.setRepositories(projectRepos);

			ArtifactResult artifactResult;
			try {
				artifactResult = repoSystem.resolveArtifact(repoSession, request);
			} catch (ArtifactResolutionException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}

			getLog().info("Resolved artifact: " + artifactReference);

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

			// add file to archive
			if (artifactFileAbsolutePath.endsWith(".jar")) {
				archiveComponentPath = "content/" + archiveComponentPath;
			}
			zipArchiver.addFile(artifactFile, archiveComponentPath);

			// collect metadata about module's java main class for exist.xml
			if (i == 0 && artifactIdentifier.contains(":jar:")) {
				components += "<resource><public-uri>http://exist-db.org/ns/expath-pkg/module-main-class</public-uri><file>"
						+ getMainClass(artifactFileAbsolutePath) + "</file></resource>";
			}
		}

		for (DefaultFileSet fileSet : fileSets) {
			zipArchiver.addFileSet(fileSet);
		}

		// collect metadata about the archive's entries
		ResourceIterator itr = zipArchiver.getResources();
		while (itr.hasNext()) {
			ArchiveEntry entry = itr.next();
			String entryPath = entry.getName();
			if (entryPath.endsWith(".jar")) {
				components += "<resource><public-uri>" + moduleNamespace + "</public-uri><file>" + entryPath.substring(8) + "</file></resource>";
			}
		}

		project.getModel().addProperty("components", components);

		// create and filter the components descriptor
		try {
			FileUtils.fileWrite(new File(archiveTmpDirectoryPath + File.separator + "components.xml"), "UTF-8", componentsTemplateFileContent);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		filterResource(archiveTmpDirectoryPath, "components.xml", descriptorsDirectoryPath, outputDirectory);

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
												this.getClass().getResource("/ro/kuberam/maven/expathPlugin/generate-descriptors.xsl").toString()),
										element(name("parameters"),
												element(name("parameter"), element(name("name"), "package-dir"),
														element(name("value"), descriptorsDirectoryPath)))))),
				executionEnvironment(project, session, pluginManager));

		// add the expath descriptors
		File descriptorsDirectory = new File(descriptorsDirectoryPath);
		for (String descriptorFileName : descriptorsDirectory.list()) {
			zipArchiver.addFile(new File(descriptorsDirectoryPath + File.separator + descriptorFileName), descriptorFileName);
		}

		try {
			zipArchiver.createArchive();
		} catch (ArchiverException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		project.getModel().addProperty("components", "");
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

}
