package ro.kuberam.maven.plugins.expath;

public class Utils {

	private static String contentDir = "content/";

	public static String processOutputDirectory(String outputDirectory) {
		if (outputDirectory == null) {
			outputDirectory = "";
		}
		outputDirectory = outputDirectory.replaceAll("^/", "");

		outputDirectory = contentDir + outputDirectory;

		if (!outputDirectory.endsWith("/")) {
			outputDirectory = outputDirectory + "/";
		}

		return outputDirectory;
	}

}
