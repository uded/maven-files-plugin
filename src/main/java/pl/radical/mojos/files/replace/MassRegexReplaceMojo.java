package pl.radical.mojos.files.replace;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @goal mass-regex-replace
 */
public class MassRegexReplaceMojo extends AbstractMassReplacerMojo {

	// private final Map<Pattern, String> replacements = new HashMap<Pattern, String>();

	@Override
	public void readProperties(final MavenProject project) throws MojoExecutionException {
		// TODO Auto-generated method stub

	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub

		// final PropertiesLoader propertiesLoader = new PropertiesLoader(actualDelimiters);
		//
		// for (final String propertyFile : filters) {
		// propertiesLoader.loadProperties(propertyFile);
		// }
		//
		// tokenValueMap.putAll(propertiesLoader.getTokens());

	}

}
