package ro.kuberam.maven.xarPlugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-lib-basics")
public class GenerateLibBasicsMojo extends AbstractMojo {

	@Parameter(required = true)
	private File libDirPath;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// create the lib directory
		if (!libDirPath.exists()) {
			libDirPath.mkdir();
		}

	}

}
