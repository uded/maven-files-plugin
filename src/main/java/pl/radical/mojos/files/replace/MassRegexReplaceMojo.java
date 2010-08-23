package pl.radical.mojos.files.replace;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @goal mass-regex-replace
 * @since 0.6.0.0
 */
public class MassRegexReplaceMojo extends AbstractMassReplacerMojo {

	protected RegExpression[] regExps;

	protected Map<Pattern, String> replacements = new HashMap<Pattern, String>();

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isDebugEnabled()) {
			getLog().debug("Preparing the list of files to be included");
		}
		final Map<File, File> files = getFileList();

		readRegexps();
		replaceTokens(files);
	}

	/**
	 * @param files
	 *            a list of files in form of a map with source and target defined
	 * @param tokenValueMap
	 *            a list of tokens and it's values to be replaced in files
	 * @throws MojoExecutionException
	 *             an exception thrown usually while reading the input file or while saving the new one
	 */
	protected void replaceTokens(final Map<File, File> files) throws MojoExecutionException {
		String content;

		if (getLog().isDebugEnabled()) {
			getLog().debug("Starting to replace file contents:");
		}

		try {
			for (final Entry<File, File> entry : files.entrySet()) {
				if (getLog().isDebugEnabled()) {
					getLog().debug("-- replacing content of " + entry.getKey().toString());
				}

				content = FileUtils.readFileToString(entry.getKey(), encoding);

				for (final Entry<Pattern, String> replacement : replacements.entrySet()) {
					content = replacement.getKey().matcher(content).replaceAll(replacement.getValue());
				}

				if (getLog().isDebugEnabled()) {
					getLog().debug("++ writing the content to " + entry.getValue());
				}
				FileUtils.writeStringToFile(entry.getValue(), content, encoding);
			}
		} catch (final IOException e) {
			getLog().error("Unable to read input file.", e);
			throw new MojoExecutionException("Unable to read input file.", e);
		}
	}

	public void readRegexps() throws MojoExecutionException {
		try {
			for (final RegExpression regExpression : regExps) {
				final String replacedPattern = regExpression.getExpression().replaceAll("\\&lt\\;", "<").replaceAll("\\&gt\\;", ">");
				final Pattern p = Pattern.compile(regExpression.getExpression());
				replacements.put(p, regExpression.getReplacement());
			}
		} catch (final PatternSyntaxException e) {
			throw new MojoExecutionException("Specified pattern was incorrect", e);
		}
	}

	@Override
	public void readProperties(final MavenProject project) throws MojoExecutionException {
		// TODO Remove in parent
	}

}
