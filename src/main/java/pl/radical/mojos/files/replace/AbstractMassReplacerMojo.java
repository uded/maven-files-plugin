package pl.radical.mojos.files.replace;

import pl.radical.mojos.files.AbstractFileMojo;
import pl.radical.mojos.replace.utils.FileRenameRegexp;
import pl.radical.mojos.replace.utils.FilesScanner;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author <a href="mailto:lukasz@radical.com.pl">Łukasz Rżanek</a>
 * @author Radical Creations &copy;2010
 */
public abstract class AbstractMassReplacerMojo extends AbstractFileMojo {

	/**
	 * It's usually a very bad idea to search-and-replace string within binary files like PNG, JPG, AVI, etc. Default
	 * exclude list contains entries for all those files. The list will also exclude folders usually used during
	 * software development, like ".svn" or ".git".
	 * 
	 * @parameter default-value=true
	 */
	protected boolean excludeDefaults;

	/**
	 * <p>
	 * The list of resources we want to replace tokenValueMap in.
	 * <p>
	 * The list should be specified in form of:
	 * 
	 * <pre>
	 * &lt;resources&gt;
	 * 	&lt;resource&gt;
	 * 		&lt;directory&gt;/a/source/path&lt;/directory&gt;
	 * 		&lt;targetPath&gt;/a/target/path&lt;/targetPath&gt;
	 * 		&lt;includes&gt;
	 * 			&lt;include&gt;*&lt;/includes&gt;
	 * 		&lt;/includes&gt;
	 * 		&lt;excludes&gt;
	 * 			&lt;exclude&gt;*.type&lt;/exclude&gt;
	 * 		&lt;/excludes&gt;
	 * 	&lt;/resource&gt;
	 * &lt;resources&gt;
	 * </pre>
	 * 
	 * @parameter
	 * @required
	 */
	protected Resource[] resources;

	/**
	 * <b>Overrides</b> the default delimiter list. The delimiters should be stated as two strings separated with pipe
	 * <b>|</b> - first the prefix and then the suffix. Anything between those two will be trated as a property name
	 * and replaced with a value of that property.
	 * 
	 * @parameter
	 */
	protected Set<String> delimiters;

	/**
	 * By defining <code>fileRenameRegexp</code> the files generated via this plugin can be renamed automatically using
	 * simple regexp.
	 * 
	 * @parameter
	 */
	protected FileRenameRegexp fileRenameRegexp;

	/**
	 * Whether to use or not use project properties. Usually it's a good thing, while you might wanto to reffer to thins
	 * like <code>${project.version}</code> automatically. But sometimes one may want to keep them separated, and this
	 * is switch to do so.
	 * 
	 * @parameter default-value=true;
	 */
	protected boolean useProjectProperties;

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

	/**
	 * Whether to ignore missing filter, so it will not cause failing the build itself.
	 * 
	 * @parameter default-value=false
	 */
	protected boolean ignoreMissingFilter;

	protected Map<String, String> tokenValueMap = new HashMap<String, String>();
	protected Set<String> actualDelimiters = new HashSet<String>();

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

	/**
	 * Scan all of the resources and return the list of files to work on
	 * 
	 * @return list of files with source and target info
	 */
	protected Map<File, File> getFileList() {
		final FilesScanner filesScanner = new FilesScanner(getLog());

		final Map<File, File> files = new HashMap<File, File>();
		for (final Resource resource : resources) {
			files.putAll(filesScanner.getFileList(resource, excludeDefaults, fileRenameRegexp));
		}
		return files;
	}

}
