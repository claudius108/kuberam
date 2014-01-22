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
import java.util.UUID;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import ro.kuberam.maven.plugins.mojos.KuberamAbstractMojo;
import ro.kuberam.maven.plugins.mojos.NameValuePair;
import ro.kuberam.maven.plugins.utils.KuberamMojoUtils;

/**
 * Transforms an EXPath specification to HTML format. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius
 *         Teodorescu</a>
 * 
 */

@Mojo(name = "transform-spec-to-html")
public class TransformSpecToHtmlMojo extends KuberamAbstractMojo {

	/**
	 * Specification file.
	 * 
	 * @parameter
	 * @since 0.3
	 * 
	 */
	@Parameter(required = true)
	private File specFile;

	/**
	 * Output directory.
	 * 
	 * @parameter
	 * @since 0.2
	 * 
	 */
	@Parameter(required = true)
	private File outputDir;

	/**
	 * Google Analytics account id, in case one needs to track the page.
	 * 
	 * @parameter
	 * @since 0.3
	 * 
	 */
	@Parameter(defaultValue = "")
	private String googleAnalyticsAccountId;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		FileUtils.mkdir(outputDir.getAbsolutePath());

		NameValuePair[] parameters = new NameValuePair[] { new NameValuePair("googleAnalyticsAccountId",
				googleAnalyticsAccountId) };

		xsltTransform(specFile,
				this.getClass().getResource("/ro/kuberam/maven/plugins/expath/xmlspec/transform-spec.xsl")
						.toString(),
				new File(outputDir + File.separator + FileUtils.basename(specFile.getAbsolutePath())
						+ "html").getAbsolutePath(), parameters);
	}

}
