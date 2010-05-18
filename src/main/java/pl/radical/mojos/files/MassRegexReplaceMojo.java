package pl.radical.mojos.files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import pl.radical.mojos.files.utils.PropertiesLoader;

/**
 * @goal mass-regex-replace
 */
public class MassRegexReplaceMojo extends AbstractMassReplacerMojo {

	private final Map<Pattern, String> replacements = new HashMap<Pattern, String>();

	@Override
	public void readProperties(final MavenProject project) throws MojoExecutionException {
		// TODO Auto-generated method stub

	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			final PropertiesLoader propertiesLoader = new PropertiesLoader(getDelimiters());

			for (final String propertyFile : filters) {
				propertiesLoader.loadProperties(propertyFile);
			}

			tokenValueMap.putAll(propertiesLoader.getTokens());
		} catch (final FileNotFoundException e) {
			getLog().error("A property was not found", e);
			throw new MojoExecutionException("A property was not found", e);
		} catch (final IOException e) {
			getLog().error("A property file cannot be read", e);
			throw new MojoExecutionException("A property file cannot be read", e);
		}
	}

}
