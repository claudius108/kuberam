package ro.kuberam.maven.expathPlugin.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * Deploys a package in a repository. <br/>
 * 
 * @author <a href="mailto:claudius.teodorescu@gmail.com">Claudius Teodorescu</a>
 * 
 */

@Mojo(name = "deploy-xar")
public class DeployXarMojo extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub

	}

}
