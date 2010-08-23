package pl.radical.mojos.files.replace;

import pl.radical.mojos.files.AbstractFileMojo;
import pl.radical.mojos.replace.utils.FileRenameRegexp;
import pl.radical.mojos.replace.utils.FilesScanner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Resource;
import org.apache.maven.project.path.PathTranslator;

/**
 * @author <a href="mailto:lukasz@radical.com.pl">Łukasz Rżanek</a>
 * @author Radical Creations &copy;2010
 */
public abstract class AbstractMassReplacerMojo extends AbstractFileMojo {

	/**
	 * Path Translator needed by the ExpressionEvaluator
	 * 
	 * @component role="org.apache.maven.project.path.PathTranslator"
	 */
	protected PathTranslator translator;

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
	 * By defining <code>fileRenameRegexp</code> the files generated via this plugin can be renamed automatically using
	 * simple regexp.
	 * 
	 * @parameter
	 */
	protected FileRenameRegexp fileRenameRegexp;

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
