package pl.radical.mojos.files.rename;

import pl.radical.mojos.replace.utils.FileRenameRegexp;
import pl.radical.mojos.replace.utils.FilesScanner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal rename-files
 * @phase process-resources
 */
public class MultiFileRenameMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 * 
	 * @parameter
	 * @required
	 */
	private Resource[] resources;

	/**
	 * @parameter
	 * @required
	 */
	protected FileRenameRegexp fileRenameRegexp;

	/**
	 * @parameter default=false
	 */
	protected boolean silent;

	public void execute() throws MojoExecutionException {
		if (getLog().isDebugEnabled()) {
			getLog().debug("Preparing the list of files to be included");
		}

		final Map<File, File> files = getFileList();

		for (final Entry<File, File> entry : files.entrySet()) {
			if (getLog().isDebugEnabled()) {
				getLog().debug(String.format("Renaming the file from [%s] to [%s]", entry.getKey().getName(), entry.getValue().getName()));
			}
			if (!entry.getKey().renameTo(entry.getValue())) {
				if (!silent) {
					throw new MojoExecutionException("It was impossible to change the name of the file from " + entry.getKey() + " to "
							+ entry.getValue());
				}
			}
		}
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
			files.putAll(filesScanner.getFileList(resource));
		}
		return files;
	}
}
