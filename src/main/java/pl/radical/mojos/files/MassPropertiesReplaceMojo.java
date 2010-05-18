package pl.radical.mojos.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import pl.radical.mojos.files.utils.PropertiesLoader;

/**
 * <p>
 * This goal will replace all occurance of the given properties within the given files. This task should be used, when a
 * regular list of properties, ie. from properties file, can be read and then used as a reference to replace all of the
 * tokenValueMap within the files. The tokenValueMap in this case should be referenced with prefix and suffix - as
 * stated on {@link #delimiters} list.
 * <p>
 * The list of files will be resolved out of the resources list.
 * 
 * @goal mass-properties-replace
 */
public class MassPropertiesReplaceMojo extends AbstractMassReplacerMojo {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public final void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isDebugEnabled()) {
			getLog().debug("Preparing the list of files to be included");
		}

		final Map<File, File> files = getFileList();

		// Start actual work
		readProperties(project);
		replaceTokens(files, tokenValueMap);
	}

	/**
	 * @param files
	 * @param tokenValueMap
	 * @throws MojoExecutionException
	 */
	protected void replaceTokens(final Map<File, File> files, final Map<String, String> tokens) throws MojoExecutionException {
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

				for (final String delimiter : getDelimiters()) {
					final String[] delims = delimiter.split("\\|");
					content = StrSubstitutor.replace(content, tokens, delims[0], delims[1]); // NOPMD
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

	@Override
	public void readProperties(final MavenProject project) throws MojoExecutionException {
		try {
			final PropertiesLoader propertiesLoader = new PropertiesLoader(getDelimiters());

			if (useMavenProperties) {
				// Maven project properties
				if (project.getProperties() != null) {
					propertiesLoader.loadProperties(project.getProperties());
				}

				if (mavenSession.getUserProperties() != null) {
					propertiesLoader.loadProperties(mavenSession.getUserProperties());
				}
			}

			if (tokens != null && !tokens.isEmpty()) {
				propertiesLoader.loadProperties(tokens);
			}

			for (final String propertyFile : filters) {
				propertiesLoader.loadProperties(propertyFile);
			}

			tokenValueMap.putAll(propertiesLoader.getTokens());

			if (getLog().isDebugEnabled()) {
				final File file = new File("target/tokenValueMap." + mavenSession.getExecutionProperties().getProperty("id") + ".properties");
				getLog().debug("Outputting the tokenValueMap currently in use to file: " + file.toString());
				propertiesLoader.writeToFile(file);
			}
		} catch (final FileNotFoundException e) {
			getLog().error("A property was not found", e);
			throw new MojoExecutionException("A property was not found", e);
		} catch (final IOException e) {
			getLog().error("A property file cannot be read", e);
			throw new MojoExecutionException("A property file cannot be read", e);
		}
	}
}
