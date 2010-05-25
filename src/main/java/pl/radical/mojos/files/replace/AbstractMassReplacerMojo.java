package pl.radical.mojos.files.replace;

import pl.radical.mojos.files.AbstractFileMojo;
import pl.radical.mojos.replace.utils.FileRenameRegexp;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;

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
	 * &lt;resource&gt;
	 * 	&lt;directory&gt;/a/source/path&lt;/directory&gt;
	 * 	&lt;targetPath&gt;/a/target/path&lt;/targetPath&gt;
	 * 	&lt;includes&gt;
	 * 		&lt;include&gt;*&lt;/includes&gt;
	 * 	&lt;/includes&gt;
	 * 	&lt;excludes&gt;
	 * 		&lt;exclude&gt;*.type&lt;/exclude&gt;
	 * 	&lt;/excludes&gt;
	 * &lt;/resource&gt;
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
	 * By defining <code>fileRenameRegexp</code> the files generated via
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

	protected Map<String, String> tokenValueMap = new HashMap<String, String>();

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
	protected Set<String> getDelimiters() throws MojoExecutionException {
		if (delimiters == null || delimiters.isEmpty()) {
			delimiters = new HashSet<String>();
			delimiters.add("@|@");
			delimiters.add("${|}");
		} else {
			for (final String delimiter : delimiters) {
				if (delimiter.matches(".*\\|.*")) {
					if (getLog().isDebugEnabled()) {
						final String[] delims = delimiter.split("\\|");
						delimiters.add(delimiter);
						getLog().debug("Added prefix: \"" + delims[0] + "\" and suffix: \"" + delims[1] + "\".");
					}
				} else {
					getLog().error("The given delimiter \"" + delimiter + "\" is in illegal form!");
					throw new MojoExecutionException("The given delimiter \"" + delimiter + "\" is in illegal form!");
				}
			}
		}
		return delimiters;
	}

	/**
	 * Scan all of the resources and return the list of files to work on
	 * 
	 * @return list of files with source and target info
	 */
	protected Map<File, File> getFileList() {
		final Map<File, File> files = new HashMap<File, File>();
		for (final Resource resource : resources) {
			files.putAll(getFileList(resource));
		}
		return files;
	}

	/**
	 * Scan the given resource to receive a full files list to work on. It should consist only of the files explicitly
	 * (or implicitly) included and excluded via plugin configuration. See {@link Resource} for details.
	 * 
	 * @param resource
	 *            a {@link Resource} entry to scan
	 * @return list of files with source and target info
	 */
	protected Map<File, File> getFileList(final Resource resource) {
		final Map<File, File> workingFiles = new HashMap<File, File>();

		final DirectoryScanner dirScanner = new DirectoryScanner();
		dirScanner.setBasedir(resource.getDirectory());

		if (resource.getIncludes().size() > 0) {
			dirScanner.setIncludes(resource.getIncludes().toArray(new String[resource.getIncludes().size()]));
		}
		if (resource.getExcludes().size() > 0) {
			if (excludeDefaults) {
				final String[] resourceExcludes = resource.getExcludes().toArray(new String[resource.getExcludes().size()]);
				final String[] excludes = (String[]) ArrayUtils.addAll(resourceExcludes, getDefaultExcludes());
				dirScanner.setExcludes(excludes);
			} else {
				dirScanner.setExcludes(resource.getExcludes().toArray(new String[resource.getExcludes().size()]));
			}
		} else {
			if (excludeDefaults) {
				dirScanner.setExcludes(getDefaultExcludes());
			}
		}

		dirScanner.addDefaultExcludes();
		dirScanner.scan();

		final String[] includedFiles = dirScanner.getIncludedFiles();
		for (final String file : includedFiles) {
			if (getLog().isDebugEnabled()) {
				getLog().debug("-- adding file: " + file);
			}

			String outputFileName;
			if (fileRenameRegexp != null) {
				outputFileName = file.replaceAll(fileRenameRegexp.getPattern(), fileRenameRegexp.getReplace() != null ? fileRenameRegexp.getReplace() : "");
			} else {
				outputFileName = file;
			}

			if (resource.getTargetPath() != null || resource.getTargetPath().length() > 0) { // NOPMD
				workingFiles.put(new File(resource.getDirectory() + "/" + file), new File(resource.getTargetPath() + "/" + outputFileName)); // NOPMD
			} else {
				workingFiles.put(new File(resource.getDirectory() + "/" + file), new File(resource.getDirectory() + "/" + outputFileName)); // NOPMD
			}
		}

		return workingFiles;
	}

	/**
	 * Creating a list of common excludes
	 * 
	 * @return a list of excludes to consider
	 */
	private String[] getDefaultExcludes() {
		final ResourceBundle excludesList = ResourceBundle.getBundle("defaultExcludes");
		final List<String> excludes = new ArrayList<String>();

		for (final Enumeration<String> keys = excludesList.getKeys(); keys.hasMoreElements();) {
			excludes.add(excludesList.getString(keys.nextElement()));
		}

		return excludes.toArray(new String[excludes.size()]);
	}

}
