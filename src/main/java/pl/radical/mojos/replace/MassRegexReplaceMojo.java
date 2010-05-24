package pl.radical.mojos.replace;

import pl.radical.mojos.replace.utils.PropertiesLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

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
		final PropertiesLoader propertiesLoader = new PropertiesLoader(getDelimiters());

		for (final String propertyFile : filters) {
			propertiesLoader.loadProperties(propertyFile);
		}

		tokenValueMap.putAll(propertiesLoader.getTokens());

	}

}
