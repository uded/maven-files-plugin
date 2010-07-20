package pl.radical.mojos.files.replace;

import pl.radical.mojos.replace.utils.PropertiesLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

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
	/**
     * <b>Overrides</b> the default delimiter list. The delimiters should be stated as two strings separated with pipe
     * <b>|</b> - first the prefix and then the suffix. Anything between those two will be trated as a property name and
     * replaced with a value of that property.
     * 
     * @parameter
     */
    protected Set<String> delimiters;
	protected Set<String> actualDelimiters = new HashSet<String>();

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public final void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isDebugEnabled()) {
			getLog().debug("Preparing the list of files to be included");
		}

		final Map<File, File> files = getFileList();

		// Start actual work
		setDelimiters();
		readProperties(project);
		replaceTokens(files, tokenValueMap);
	}

	/**
	 * @param files
	 *            a list of files in form of a map with source and target defined
	 * @param tokenValueMap
	 *            a list of tokens and it's values to be replaced in files
	 * @throws MojoExecutionException
	 *             an exception thrown usually while reading the input file or while saving the new one
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

				for (final String delimiter : actualDelimiters) {
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
			final PropertiesLoader propertiesLoader = new PropertiesLoader(actualDelimiters);

			// TODO PluginParameterExpressionEvaluator to evaluate all of the M2 props
			// final PluginParameterExpressionEvaluator ppee = new PluginParameterExpressionEvaluator(mavenSession, ,
			// translator, getLog(), project, project
			// .getProperties());

			if (useProjectProperties) {
				// Maven project properties
				if (project.getProperties() != null && project.getProperties().size() > 0) {
					propertiesLoader.loadProperties(project.getProperties());
				}

				if (mavenSession.getExecutionProperties() != null) {
					propertiesLoader.loadProperties(mavenSession.getExecutionProperties());
				}
				if (mavenSession.getUserProperties() != null) {
					propertiesLoader.loadProperties(mavenSession.getUserProperties());
				}
			}

			if (tokens != null && !tokens.isEmpty()) {
				propertiesLoader.loadProperties(tokens);
			}

			for (final String propertyFile : filters) {
				propertiesLoader.loadProperties(new File(propertyFile));
			}

			tokenValueMap.putAll(propertiesLoader.getTokens());

			if (getLog().isDebugEnabled()) {
				// TODO Execution ID would be nice
				// String id = mavenSession.getExecutionProperties().getProperty("id");
				final String id = Integer.toString(new Random().nextInt(1000));
				final File file = new File("target/tokenValueMap." + id + ".properties");
				getLog().debug("Outputting the tokenValueMap currently in use to file: " + file.toString());
				propertiesLoader.writeToFile(file);
			}
		} catch (final FileNotFoundException e) {
			if (!ignoreMissingFilter) {
				getLog().error("A property was not found", e);
				throw new MojoExecutionException("A property was not found", e);
			}
		} catch (final IOException e) {
			if (!ignoreMissingFilter) {
				getLog().error("A property file cannot be read", e);
				throw new MojoExecutionException("A property file cannot be read", e);
			}
		}
	}

	/**
     * <p>
     * Get the list of delimiters (proefix and suffix) to be used during string replacement.
     * <p>
     * If no list was entered by the user, the method will return a list of default delimiters.
     * 
     * @return list of delimiters (prefix|suffix)
     * @throws MojoExecutionException
     *             if delimiter was not entered in correct form
     */
    protected void setDelimiters() throws MojoExecutionException {
    	final Set<String> workingDelimiters = new HashSet<String>();
    	if (delimiters == null || delimiters.isEmpty()) {
    		workingDelimiters.add("@|@");
    		workingDelimiters.add("${|}");
    	} else {
    		for (final String delimiter : delimiters) {
    			if (delimiter.matches(".*\\|.*")) {
    				if (getLog().isDebugEnabled()) {
    					final String[] delims = delimiter.split("\\|");
    					workingDelimiters.add(delimiter);
    					getLog().debug("Added prefix: \"" + delims[0] + "\" and suffix: \"" + delims[1] + "\".");
    				}
    			} else {
    				getLog().error("The given delimiter \"" + delimiter + "\" is in illegal form!");
    				throw new MojoExecutionException("The given delimiter \"" + delimiter + "\" is in illegal form!");
    			}
    		}
    	}
    	actualDelimiters.addAll(workingDelimiters);
    }
}
