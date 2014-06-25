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
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
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
		if(!descriptor.exists()) {
			throw new MojoExecutionException("Global descriptor file '" + descriptor.getAbsolutePath()
					+ "' does not exist.");
		}

		// set needed variables
		final String outputDirectoryPath = outputDir.getAbsolutePath();
		final String assemblyDescriptorName = descriptor.getName();
		final String archiveTmpDirectoryPath = projectBuildDirectory + File.separator + "make-xar-tmp";

		final String descriptorsDirectoryPath = outputDirectoryPath + File.separator + "expath-descriptors-"
				+ UUID.randomUUID();

		// Plugin xarPlugin =
		// project.getPlugin("ro.kuberam.maven.plugins:kuberam-xar-plugin");
		// DescriptorConfiguration mainConfig = new
		// DescriptorConfiguration((Xpp3Dom) xarPlugin.getConfiguration());

		// filter the descriptor file
		filterResource(descriptor.getParent(), assemblyDescriptorName, archiveTmpDirectoryPath, outputDir);
		final File filteredDescriptor = new File(archiveTmpDirectoryPath + File.separator
				+ assemblyDescriptorName);

		// get the execution configuration
		FileReader fileReader = null;
		final DescriptorConfiguration executionConfig;
		try {
			fileReader = new FileReader(filteredDescriptor);
			executionConfig = new DescriptorConfiguration(Xpp3DomBuilder.build(fileReader));
		} catch(final IOException ioe) {
		    throw new MojoExecutionException(ioe.getMessage());
        } catch(final XmlPullParserException xppe) {
            throw new MojoExecutionException(xppe.getMessage());
        } finally {
            if(fileReader != null) {
                try {
                    fileReader.close();
                } catch(final IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

		// extract settings from execution configuration
		final List<DefaultFileSet> fileSets = executionConfig.getFileSets();
		final List<DependencySet> dependencySets = executionConfig.getDependencySets();
		final String moduleNamespace = executionConfig.getModuleNamespace();

		// set the zip archiver
		zipArchiver.setCompress(true);
		zipArchiver.setDestFile(new File(outputDirectoryPath + File.separator + finalName + ".xar"));
		zipArchiver.setForced(true);

        final Set<ComponentResource> componentResources = new HashSet<ComponentResource>();

		// process the maven type dependencies
		for(int i = 0; i < dependencySets.size(); i++) {
			final DependencySet dependencySet = dependencySets.get(i);

            try {
			    // define the artifact
			    final Artifact artifactReference = new DefaultArtifact(dependencySet.getGroupId() + ":"
						+ dependencySet.getArtifactId() + ":" + dependencySet.getVersion());

                final String artifactIdentifier = artifactReference.toString();
                getLog().info("Resolving artifact: " + artifactReference);

                // resolve the artifact
                final ArtifactRequest request = new ArtifactRequest();
                request.setArtifact(artifactReference);
                request.setRepositories(projectRepos);

                final ArtifactResult artifactResult = repoSystem.resolveArtifact(repoSession, request);

                getLog().info("Resolved artifact: " + artifactReference);

                final Artifact artifact = artifactResult.getArtifact();
                final File artifactFile = artifact.getFile();
                final String artifactFileAbsolutePath = artifactFile.getAbsolutePath();
                final String artifactFileName = artifactFile.getName();
                final String dependencySetOutputDirectory = dependencySet.getOutputDirectory();

                String archiveComponentPath;
                if(dependencySetOutputDirectory == null || dependencySetOutputDirectory.equals("/")) {
                    archiveComponentPath = artifactFileName;
                } else {
                    archiveComponentPath = dependencySetOutputDirectory + File.separator + artifactFileName;
                }

                // add file to archive
                if(artifactFileAbsolutePath.endsWith(".jar")) {
                    archiveComponentPath = "content/" + archiveComponentPath;
                }
                zipArchiver.addFile(artifactFile, archiveComponentPath);

                // collect metadata about module's java main class for exist.xml
                if(i == 0 && artifactIdentifier.contains(":jar:")) {
                    componentResources.add(new ComponentResource("http://exist-db.org/ns/expath-pkg/module-main-class", getMainClass(artifactFileAbsolutePath).get(0)));
                    componentResources.add(new ComponentResource("http://exist-db.org/ns/expath-pkg/module-namespace", getMainClass(artifactFileAbsolutePath).get(1)));
                }
            } catch(final IllegalArgumentException iae) {
                throw new MojoFailureException(iae.getMessage(), iae);
            } catch(final ArtifactResolutionException are) {
                throw new MojoExecutionException(are.getMessage(), are);
            }
		}

		for(final DefaultFileSet fileSet : fileSets) {
			zipArchiver.addFileSet(fileSet);
		}

		// collect metadata about the archive's entries
		final ResourceIterator itr = zipArchiver.getResources();
		while(itr.hasNext()) {
			final ArchiveEntry entry = itr.next();
			final String entryPath = entry.getName();
			if(entryPath.endsWith(".jar")) {
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

		} catch(final SaxonApiException sae) {
			sae.printStackTrace();
        } catch(final SAXException saxe) {
            saxe.printStackTrace();
        }

		// generate the expath descriptors
		final NameValuePair[] parameters = new NameValuePair[] { new NameValuePair("package-dir",
                descriptorsDirectoryPath) };

		xsltTransform(filteredDescriptor,
				this.getClass().getResource("/ro/kuberam/maven/plugins/expath/generate-descriptors.xsl")
						.toString(), descriptorsDirectoryPath, parameters);

		// add the expath descriptors
		final File descriptorsDirectory = new File(descriptorsDirectoryPath);
		for(final String descriptorFileName : descriptorsDirectory.list()) {
			zipArchiver.addFile(new File(descriptorsDirectoryPath + File.separator + descriptorFileName),
					descriptorFileName);
		}

		try {
			zipArchiver.createArchive();
		} catch (final ArchiverException ae) {
			ae.printStackTrace();
		} catch(final IOException ioe) {
			ioe.printStackTrace();
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

	private static List<String> getMainClass(final String firstDependencyAbsolutePath) {
		final List<String> result = new ArrayList<String>();
		try {
			final URL u = new URL("jar", "", "file://" + firstDependencyAbsolutePath + "!/");
			final JarURLConnection uc = (JarURLConnection) u.openConnection();
			final Attributes attr = uc.getMainAttributes();
            result.add(attr.getValue(Attributes.Name.MAIN_CLASS));
            result.add(attr.getValue("ModuleNamespace"));

		} catch(final Exception e1) {
			e1.printStackTrace();
		}

		return result;
	}

}

