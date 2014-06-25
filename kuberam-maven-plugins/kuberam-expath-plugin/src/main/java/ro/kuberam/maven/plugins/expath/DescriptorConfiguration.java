package ro.kuberam.maven.plugins.expath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class DescriptorConfiguration extends Xpp3Dom {

    private static final long serialVersionUID = -8323628485538303936L;

	public DescriptorConfiguration(final Xpp3Dom src) {
		super(src);
	}

	public List<DefaultFileSet> getFileSets() {
		final List<DefaultFileSet> fileSets = new ArrayList<DefaultFileSet>();

        final Xpp3Dom fileSetsElement = getChild("fileSets");
		if(fileSetsElement != null) {
			final Xpp3Dom[] fileSetChildren = fileSetsElement.getChildren("fileSet");
			for(final Xpp3Dom fileSetChild : fileSetChildren) {
                final DefaultFileSet fileSet = getFileSet(fileSetChild);
                fileSets.add(fileSet);
			}
		}

		return fileSets;
	}

    private DefaultFileSet getFileSet(final Xpp3Dom fileSetChild) {
        final DefaultFileSet fileSet = new DefaultFileSet();

        fileSet.setDirectory(new File(fileSetChild.getChild("directory").getValue()));

        Xpp3Dom outputDirectoryElement = fileSetChild.getChild("outputDirectory");

        String outputDirectory = "";
        if(outputDirectoryElement != null) {
            outputDirectory = outputDirectoryElement.getValue();
        }
        if(!outputDirectory.isEmpty() && !outputDirectory.endsWith("/")) {
            outputDirectory += '/';
        }
        fileSet.setPrefix(outputDirectory);

        //process includes
        final Xpp3Dom includesElement = fileSetChild.getChild("includes");
        final List<String> includes = extractChildValues(includesElement);
        if(includes.isEmpty()) {
            fileSet.setIncludes(new String[]{"**/*.*"});
        } else {
            fileSet.setIncludes(includes.toArray(new String[includes.size()]));
        }

        //process excludes
        final Xpp3Dom excludesElement = fileSetChild.getChild("excludes");
        final List<String> excludes = new ArrayList<String>() {{
            add(".project/,.settings/");
            addAll(extractChildValues(excludesElement));
        }};
        fileSet.setExcludes(excludes.toArray(new String[excludes.size()]));

        return fileSet;
    }

    /**
     * Given a container element with a plural name
     * this function extracts all the children of elements
     * with the singular name and put's them into a comma
     * separated string. For example, given:
     *
     * <includes>
     *     <include>a</include>
     *     <include>b</include>
     * </includes>
     *
     * This function would return the result:
     *  List<String>("a", "b")
     *
     *  @param container An element with a plural name, containing
     *                   child elements with singular names
     *
     *  @return A comma separated string of the child element values
     */
    private List<String> extractChildValues(final Xpp3Dom container) {

        final List<String> values = new ArrayList<String>();

        if(container != null) {
            final String childName = container.getName().substring(0, container.getName().length() - 1);
            final Xpp3Dom[] children = container.getChildren(childName);

            for(final Xpp3Dom childElement : children) {
               values.add(childElement.getValue());
            }
        }

        return values;
    }

	public List<DependencySet> getDependencySets() {
		final List<DependencySet> dependencySets = new ArrayList<DependencySet>();

        final Xpp3Dom dependencySetsElement = getChild("dependencySets");
        if(dependencySetsElement != null) {
			final Xpp3Dom[] dependencySetChildren = dependencySetsElement.getChildren("dependencySet");
			for(final Xpp3Dom dependencySetChild : dependencySetChildren) {
				final String outputDirectory = dependencySetChild.getChild("outputDirectory") != null ? dependencySetChild.getChild("outputDirectory").getValue() : "/";
				dependencySets.add(new DependencySet(
                        dependencySetChild.getChild("groupId").getValue(),
						dependencySetChild.getChild("artifactId").getValue(),
                        dependencySetChild.getChild("version").getValue(),
                        outputDirectory));
			}
		}

		return dependencySets;
	}
	
	public String getModuleNamespace() {
		final Xpp3Dom moduleNamespaceElement = getChild("module-namespace");
		if(moduleNamespaceElement != null) {
			return moduleNamespaceElement.getValue();
		} else {
            return "";
        }
	}
}


