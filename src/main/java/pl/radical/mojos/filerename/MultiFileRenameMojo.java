package pl.radical.mojos.filerename;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;

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

		for (Entry<File, File> entry : files.entrySet()) {
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
			dirScanner.setExcludes(resource.getExcludes().toArray(new String[resource.getExcludes().size()]));
		}

		dirScanner.addDefaultExcludes();
		dirScanner.scan();

		final String[] includedFiles = dirScanner.getIncludedFiles();
		for (final String file : includedFiles) {
			if (getLog().isDebugEnabled()) {
				getLog().debug("-- adding file: " + file);
			}

			String outputFileName = file.replaceAll(fileRenameRegexp.getPattern(), fileRenameRegexp.getReplace() != null ? fileRenameRegexp
					.getReplace() : "");
			workingFiles.put(new File(resource.getDirectory() + "/" + file), new File(resource.getDirectory() + "/" + outputFileName)); // NOPMD
		}

		return workingFiles;
	}
}
