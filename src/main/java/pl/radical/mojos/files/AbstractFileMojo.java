package pl.radical.mojos.files;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Abstract class defining the base of operations for all of the mojos.
 * 
 * @author <a href="mailto:lukasz@radical.com.pl">Łukasz Rżanek</a>
 * @author Radical Creations &copy;2010
 * @phase process-resources
 */
public abstract class AbstractFileMojo extends AbstractMojo {
	/**
	 * Internal Maven's project information
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The Maven Session Object
	 * 
	 * @parameter expression="${session}"
	 * @readonly
	 */
	protected MavenSession mavenSession;

	/**
	 * Encoding that should be used while copying the files.
	 * 
	 * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
	 */
	protected String encoding;

	public abstract void readProperties(final MavenProject project) throws MojoExecutionException;
}
