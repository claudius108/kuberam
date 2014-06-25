package ro.kuberam.maven.plugins.expath;

/**
 * 
 * A dependencySet allows inclusion and exclusion of project dependencies in the
 * assembly.
 * 
 * 
 * @version $Revision$ $Date$
 */
public class DependencySet {

	private final String groupId;
	private final String artifactId;
	private final String version;
	private final String outputDirectory;

	public DependencySet(final String groupId, final String artifactId, final String version, final String outputDirectory) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.outputDirectory = outputDirectory;
	}

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version + " " + outputDirectory;
    }
}

