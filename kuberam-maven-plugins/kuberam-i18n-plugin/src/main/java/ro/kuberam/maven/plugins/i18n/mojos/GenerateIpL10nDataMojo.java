package ro.kuberam.maven.plugins.i18n.mojos;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystemSession;

import ro.kuberam.maven.plugins.mojos.KuberamAbstractMojo;

/**
 * Generates the internationalization data. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius
 *         Teodorescu</a>
 * 
 */

@Mojo(name = "generate-ip-l10n-data")
public class GenerateIpL10nDataMojo extends KuberamAbstractMojo {

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setRepoSession(RepositorySystemSession repoSession) {
		this.repoSession = repoSession;
	}

	public void setProjectBuildDirectory(File projectBuildDirectory) {
		this.projectBuildDirectory = projectBuildDirectory;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// make output directory
		File outputDirectory = new File(projectBuildDirectory + File.separator + "java");
		outputDirectory.mkdir();
		// download ip to country database
		URL url;
		HttpURLConnection connection;
		File ip2countryFile = null;
		int BUFFER = 2048;

		try {
			url = new URL("http://madm.dfki.de/demo/ip-countryside/ip2country.zip");
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("content-type", "binary/data");
			InputStream in = connection.getInputStream();
			ip2countryFile = File.createTempFile("ip2country", ".zip");
			FileOutputStream out = new FileOutputStream(ip2countryFile);

			byte[] b = new byte[BUFFER];
			int count;

			while ((count = in.read(b)) > 0) {
				out.write(b, 0, count);
			}
			out.close();
			in.close();

			// unzip the database
			ZipFile ip2countryZipFile = new ZipFile(ip2countryFile);

			ZipEntry ip2countryEntry = ip2countryZipFile.getEntry("ip2country.db");
			BufferedInputStream is = new BufferedInputStream(ip2countryZipFile.getInputStream(ip2countryEntry));
			int currentByte;
			byte data[] = new byte[BUFFER];

			File ip2countryDb = File.createTempFile("ip2country", ".db");
			FileOutputStream fos = new FileOutputStream(ip2countryDb);
			BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

			while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, currentByte);
			}
			dest.flush();
			dest.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Properties properties = new Properties();
			properties.setProperty("favoriteAnimal", "marmot");
			properties.setProperty("favoriteContinent", "Antarctica");
			properties.setProperty("favoritePerson", "Nicole");

			File file = new File(outputDirectory + File.separator + "country-alpha-3-codes.properties");
			FileOutputStream fileOut = new FileOutputStream(file);
			properties.store(fileOut, "country alpha-2 code=country alpha-3 code");
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[] startRanges = { 16777216, 16777472, 16778240, 16779264, 16781312, 16785408, 16793600 };

		System.out.println(Arrays.binarySearch(startRanges, 16779265));
	}

	public static void main(String[] args) throws Exception {
		// download ip to country database
		URL url;
		HttpURLConnection connection;
		File ip2countryFile = null;
		int BUFFER = 2048;

		try {
			url = new URL("http://madm.dfki.de/demo/ip-countryside/ip2country.zip");
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("content-type", "binary/data");
			InputStream in = connection.getInputStream();
			ip2countryFile = File.createTempFile("ip2country", ".zip");
			FileOutputStream out = new FileOutputStream(ip2countryFile);

			byte[] b = new byte[BUFFER];
			int count;

			while ((count = in.read(b)) > 0) {
				out.write(b, 0, count);
			}
			out.close();
			in.close();

			// unzip the database
			ZipFile ip2countryZipFile = new ZipFile(ip2countryFile);

			ZipEntry ip2countryEntry = ip2countryZipFile.getEntry("ip2country.db");
			BufferedInputStream is = new BufferedInputStream(ip2countryZipFile.getInputStream(ip2countryEntry));
			int currentByte;
			byte data[] = new byte[BUFFER];

			File ip2countryDb = File.createTempFile("ip2country", ".db");
			FileOutputStream fos = new FileOutputStream(ip2countryDb);
			BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

			while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, currentByte);
			}
			dest.flush();
			dest.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Properties properties = new Properties();
			properties.setProperty("favoriteAnimal", "marmot");
			properties.setProperty("favoriteContinent", "Antarctica");
			properties.setProperty("favoritePerson", "Nicole");

			File file = new File("src/main/resources/org/expath/location/providers/implicit/country-alpha-3-codes.properties");
			FileOutputStream fileOut = new FileOutputStream(file);
			properties.store(fileOut, "country alpha-2 code=country alpha-3 code");
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[] startRanges = { 16777216, 16777472, 16778240, 16779264, 16781312, 16785408, 16793600 };

		System.out.println(Arrays.binarySearch(startRanges, 16779265));
	}

}
