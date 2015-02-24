package ro.kuberam.maven.plugins.expath;

public class Utils {

	public static String processOutputDirectory(String outputDirectory) {
		if (outputDirectory == null) {
			outputDirectory = "";
		}
		outputDirectory = outputDirectory.replaceAll("^/", "");

		outputDirectory = "content/" + outputDirectory;

		if (!outputDirectory.endsWith("/")) {
			outputDirectory = outputDirectory + "/";
		}

		return outputDirectory;
	}

}
