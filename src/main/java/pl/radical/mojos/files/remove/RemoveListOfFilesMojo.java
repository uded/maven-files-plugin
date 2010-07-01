package pl.radical.mojos.files.remove;

import pl.radical.mojos.files.AbstractFileMojo;
import pl.radical.mojos.replace.utils.FilesScanner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @author <a href="mailto:lukasz@radical.com.pl">Łukasz Rżanek</a>
 * @author Radical Creations &copy;2010
 * @goal remove-files
 */
public class RemoveListOfFilesMojo extends AbstractFileMojo {
	/**
	 * Location of the file.
	 * 
	 * @parameter
	 * @required
	 */
	private Resource[] resources;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getLog().isDebugEnabled()) {
			getLog().debug("Preparing the list of files to be included");
		}

		final Map<File, File> files = getFileList();

		for (final Entry<File, File> entry : files.entrySet()) {
			if (getLog().isDebugEnabled()) {
				getLog().debug(String.format("Removing file [%s]", entry.getKey().getName()));
			}
			FileUtils.deleteQuietly(entry.getKey());
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

	@Override
	public void readProperties(final MavenProject project) throws MojoExecutionException {
		// TODO Auto-generated method stub
	}

}
