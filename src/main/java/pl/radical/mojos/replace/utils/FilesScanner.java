package pl.radical.mojos.replace.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;

public class FilesScanner {
	private final Log logger;

	public FilesScanner(final Log logger) {
		this.logger = logger;
	}

	public final Map<File, File> getFileList(final Resource resource) {
		return getFileList(resource, false, null);
	}

	/**
	 * Scan the given resource to receive a full files list to work on. It should consist only of the files explicitly
	 * (or implicitly) included and excluded via plugin configuration. See {@link Resource} for details. Additionally,
	 * this method will filters resulted target file names via provided RegExp filter.
	 * 
	 * @param resource
	 *            a {@link Resource} entry to scan
	 * @return list of files with source and target info
	 */
	public final Map<File, File> getFileList(final Resource resource, final boolean excludeDefaults, final FileRenameRegexp fRenameRegxp) {
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
			if (logger.isDebugEnabled()) {
				logger.debug("-- adding file: " + file);
			}

			String outputFileName;
			if (fRenameRegxp != null) {
				outputFileName = file
				.replaceAll(fRenameRegxp.getPattern(), fRenameRegxp.getReplace() != null ? fRenameRegxp.getReplace() : "");
			} else {
				outputFileName = file;
			}

			if (resource.getTargetPath() != null || resource.getTargetPath().length() == 0) { // NOPMD
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
