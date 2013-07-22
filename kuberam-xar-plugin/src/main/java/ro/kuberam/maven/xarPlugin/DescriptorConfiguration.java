package ro.kuberam.maven.xarPlugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class DescriptorConfiguration extends Xpp3Dom {

	public DescriptorConfiguration(Xpp3Dom src) {
		super(src);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8323628485538303936L;

	public List<FileSet> getFileSets() {
		List<FileSet> fileSets = new ArrayList<FileSet>();
		Xpp3Dom fileSetsElement = this.getChild("fileSets");
		if (null != fileSetsElement) {
			Xpp3Dom[] fileSetChildren = fileSetsElement.getChildren("fileSet");
			for (Xpp3Dom fileSetChild : fileSetChildren) {
				FileSet fileSet = new FileSet();
				fileSet.setDirectory(fileSetChild.getChild("directory").getValue());
				fileSet.setOutputDirectory(fileSetChild.getChild("outputDirectory").getValue());
				fileSets.add(fileSet);
			}
		}

		return fileSets;
	}

	public List<DependencySet> getDependencySets() {
		List<DependencySet> dependencySets = new ArrayList<DependencySet>();
		Xpp3Dom dependencySetsElement = this.getChild("dependencySets");
		if (null != dependencySetsElement) {
			Xpp3Dom[] dependencySetChildren = dependencySetsElement.getChildren("dependencySet");
			for (Xpp3Dom dependencySetChild : dependencySetChildren) {
				String outputDirectory = (null != dependencySetChild.getChild("outputDirectory")) ? dependencySetChild.getChild("outputDirectory").getValue() : "/";
				dependencySets.add(new DependencySet(dependencySetChild.getChild("groupId").getValue(),
						dependencySetChild.getChild("artifactId").getValue(), dependencySetChild.getChild("version")
								.getValue(), outputDirectory));
			}
		}

		return dependencySets;
	}
	
	public String getModuleNamespace() {
		Xpp3Dom moduleNamespaceElement = this.getChild("module-namespace");
		if (null != moduleNamespaceElement) {
			return moduleNamespaceElement.getValue();
		}
		return "";
	}
	
}
