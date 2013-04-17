package ro.kuberam.maven.xarPlugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

public class GenerateDescriptorsMojo {

	/**
	 * Says "Hi" to the user.
	 * 
	 */
	@Mojo(name = "generate-descriptors")
	public class GreetingMojo extends AbstractMojo {
		public void execute() throws MojoExecutionException {
			getLog().info("Hello, world.");
		}
	}

}
