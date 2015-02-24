package ro.kuberam.maven.plugins.expath;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class Utils {

	private static String contentDir = "content/";

	public static String getOutputDirectory(Xpp3Dom parentElement) {
		Xpp3Dom outputDirectoryElement = parentElement.getChild("outputDirectory");

		String outputDirectory = "";
		if (null != outputDirectoryElement) {
			outputDirectory = outputDirectoryElement.getValue();
		} else {
			
		}

		outputDirectory = outputDirectory.replaceAll("^/", "");

		outputDirectory = contentDir + outputDirectory;

		if (!outputDirectory.endsWith("/")) {
			outputDirectory = outputDirectory + "/";
		}

		return outputDirectory;
	}

}
