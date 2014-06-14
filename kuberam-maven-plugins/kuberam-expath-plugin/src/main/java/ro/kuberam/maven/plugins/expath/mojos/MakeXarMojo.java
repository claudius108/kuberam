package ro.kuberam.maven.plugins.expath.mojos;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import ro.kuberam.maven.plugins.expath.DefaultFileSet;
import ro.kuberam.maven.plugins.expath.DependencySet;
import ro.kuberam.maven.plugins.expath.DescriptorConfiguration;
import ro.kuberam.maven.plugins.mojos.KuberamAbstractMojo;
import ro.kuberam.maven.plugins.mojos.NameValuePair;

/**
 * Assembles a package. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius
 *         Teodorescu</a>
 * 
 */

@Mojo(name = "make-xar")
public class MakeXarMojo extends KuberamAbstractMojo {

    @Parameter(required = true)
	private File descriptor;

	@Parameter(defaultValue = "${project.build.directory}")
	private File outputDir;

	@Component(role = org.codehaus.plexus.archiver.Archiver.class, hint = "zip")
	private ZipArchiver zipArchiver;

	@Component
	private RepositorySystem repoSystem;

    private final static String NS_EXPATH_PKG = "http://exist-db.org/ns/expath-pkg";
    final Processor processor = new Processor(new Configuration());


	public void execute() throws MojoExecutionException, MojoFailureException {

		// test if descriptor file exists
		if (!descriptor.exists()) {
			throw new MojoExecutionException("Global descriptor file '" + descriptor.getAbsolutePath()
					+ "' does not exist.");
		}

		// set needed variables
		String outputDirectoryPath = outputDir.getAbsolutePath();
		String assemblyDescriptorName = descriptor.getName();
		String archiveTmpDirectoryPath = projectBuildDirectory + File.separator + "make-xar-tmp";

		String descriptorsDirectoryPath = outputDirectoryPath + File.separator + "expath-descriptors-"
				+ UUID.randomUUID();

		// Plugin xarPlugin =
		// project.getPlugin("ro.kuberam.maven.plugins:kuberam-xar-plugin");
		// DescriptorConfiguration mainConfig = new
		// DescriptorConfiguration((Xpp3Dom) xarPlugin.getConfiguration());

		// filter the descriptor file
		filterResource(descriptor.getParent(), assemblyDescriptorName, archiveTmpDirectoryPath, outputDir);
		File filteredDescriptor = new File(archiveTmpDirectoryPath + File.separator
				+ assemblyDescriptorName);

		// get the execution configuration
		FileReader fileReader;
		DescriptorConfiguration executionConfig;
		try {
			fileReader = new FileReader(filteredDescriptor);
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

        final Set<ComponentResource> componentResources = new HashSet<ComponentResource>();

		// process the maven type dependencies
		for (int i = 0, il = dependencySets.size(); i < il; i++) {
			DependencySet dependencySet = dependencySets.get(i);

			// define the artifact
			Artifact artifactReference;
			try {
				artifactReference = new DefaultArtifact(dependencySet.groupId + ":"
						+ dependencySet.artifactId + ":" + dependencySet.version);
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
                componentResources.add(new ComponentResource("http://exist-db.org/ns/expath-pkg/module-main-class", getMainClass(artifactFileAbsolutePath).get(0)));
                componentResources.add(new ComponentResource("http://exist-db.org/ns/expath-pkg/module-namespace", getMainClass(artifactFileAbsolutePath).get(1)));
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
                componentResources.add(new ComponentResource(moduleNamespace, entryPath.substring(8)));
			}
		}

		// create and filter the components descriptor
		final File componentsTemplateFile = new File(archiveTmpDirectoryPath + File.separator + "components.xml");
		try {
            //write the components descriptor document
            final XdmNode componentsDoc = getComponentsDoc(componentResources);
            final Serializer serializer = processor.newSerializer(componentsTemplateFile);
            serializer.setOutputProperty(Serializer.Property.ENCODING, "UTF-8");
            serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "no");
            serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
            serializer.serializeNode(componentsDoc);
            serializer.close();

            //filter the components descriptor
            filterResource(archiveTmpDirectoryPath, "components.xml", descriptorsDirectoryPath, outputDir);

		} catch (final SaxonApiException sae) {
			sae.printStackTrace();
        } catch (final SAXException saxe) {
            saxe.printStackTrace();
        }

		// generate the expath descriptors
		NameValuePair[] parameters = new NameValuePair[] { new NameValuePair("package-dir",
				descriptorsDirectoryPath) };

		xsltTransform(filteredDescriptor,
				this.getClass().getResource("/ro/kuberam/maven/plugins/expath/generate-descriptors.xsl")
						.toString(), descriptorsDirectoryPath, parameters);

		// add the expath descriptors
		File descriptorsDirectory = new File(descriptorsDirectoryPath);
		for (String descriptorFileName : descriptorsDirectory.list()) {
			zipArchiver.addFile(new File(descriptorsDirectoryPath + File.separator + descriptorFileName),
					descriptorFileName);
		}

		try {
			zipArchiver.createArchive();
		} catch (ArchiverException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

    /**
     * Programatically constructs an XML Document in memory
     * using Saxon to represent a components document.
     *
     * @param resources The resources to place in the component
     *                  document, or the empty set.
     *
     * @return The component document
     */
    private XdmNode getComponentsDoc(final Set<ComponentResource> resources) throws SaxonApiException, SAXException {
        final DocumentBuilder builder = processor.newDocumentBuilder();
        final BuildingContentHandler handler = builder.newBuildingContentHandler();

        final org.xml.sax.Attributes emptyAttributes = new AttributesImpl();

        handler.startDocument();
        handler.startPrefixMapping("", NS_EXPATH_PKG);
        handler.startElement(NS_EXPATH_PKG, "package", "package", emptyAttributes);

        for(final ComponentResource resource : resources) {
            handler.startElement(NS_EXPATH_PKG, "resource", "resource", emptyAttributes);

                //<public-uri>...</public-uri>
                handler.startElement(NS_EXPATH_PKG, "public-uri", "public-uri", emptyAttributes);
                final char[] publicUri = resource.getPublicUri().toCharArray();
                handler.characters(publicUri, 0, publicUri.length);
                handler.endElement(NS_EXPATH_PKG, "public-uri", "public-uri");

                //<resource>...</resource>
                handler.startElement(NS_EXPATH_PKG, "file", "file", emptyAttributes);
                final char[] file = resource.getFile().toCharArray();
                handler.characters(file, 0, file.length);
                handler.endElement(NS_EXPATH_PKG, "file", "file");

            handler.endElement(NS_EXPATH_PKG, "resource", "resource");
        }

        handler.endElement(NS_EXPATH_PKG, "package", "package");
        handler.endPrefixMapping("");
        handler.endDocument();

        return handler.getDocumentNode();
    }

    /**
     * Tuple class
     * for use with Component Resources
     */
    private class ComponentResource {
        final String publicUri;
        final String file;

        public ComponentResource(final String publicUri, final String file) {
            this.publicUri = publicUri;
            this.file = file;
        }

        public final String getPublicUri() {
            return publicUri;
        }

        public final String getFile() {
            return file;
        }
    }

	private static List<String> getMainClass(String firstDependencyAbsolutePath) {
		List<String> result = new ArrayList<String>();

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

		result.add(attr.getValue(Attributes.Name.MAIN_CLASS));
		result.add(attr.getValue("ModuleNamespace"));

		return result;
	}

}
