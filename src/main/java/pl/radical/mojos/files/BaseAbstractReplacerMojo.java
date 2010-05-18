package pl.radical.mojos.files;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Abstract class defining the base of operations for replacing the files.
 * 
 * @author <a href="mailto:lukasz@radical.com.pl">Łukasz Rżanek</a>
 * @author Radical Creations &copy;2010
 * @phase process-resources
 */
public abstract class BaseAbstractReplacerMojo extends AbstractMojo {
	/**
	 * Internal Maven's project information
	 * 
	 * @parameter expression="${project}"
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

	/**
	 * Whether to use or not use project properties. Usually it's a good thing, while you might wanto to reffer to thins
	 * like <code>${project.version}</code> automatically. But sometimes one may want to keep them separated, and this
	 * is switch to do so.
	 * 
	 * @parameter default-value=true;
	 */
	protected boolean useMavenProperties;

	/**
	 * List of tokens, in form of name-value pair, that should be replaced in given file(s).
	 * 
	 * @properties
	 */
	protected Properties tokens;

	/**
	 * List of properties files to read the token and values from.
	 * 
	 * @parameter
	 */
	protected List<String> filters;

	protected Map<String, String> tokenValueMap = new HashMap<String, String>();

	/**
	 * <p>
	 * If the tokenValueMap are in form of regular expressions pattern, this property should change to true. Be carefull
	 * though, patterns are tricky :-)
	 * <p>
	 * This option cannot be used with {@link #useDelimiters}
	 * 
	 * @parameter default-value=false
	 */
	protected boolean tokenRegexp;

	public abstract void readProperties(final MavenProject project) throws MojoExecutionException;
}
