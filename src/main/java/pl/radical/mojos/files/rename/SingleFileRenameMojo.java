package pl.radical.mojos.files.rename;

import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal rename-file
 * @phase process-resources
 */
public class SingleFileRenameMojo extends AbstractMojo {

	/**
	 * @parameter
	 * @required
	 */
	protected Set<FileDefinition> filesDefinitions;

	public void execute() throws MojoExecutionException, MojoFailureException {
		for (FileDefinition fd : filesDefinitions) {
			if (getLog().isDebugEnabled()) {
				getLog().debug(String.format("Renaming the file from [%s] to [%s]", fd.getInputFile(), fd.getOutputFile()));
			}

			if (fd.getOutputFile().exists() && fd.isForceOverwrite()) {
				if (!fd.getOutputFile().isFile()) {
					throw new MojoExecutionException("The given outpufile " + fd.getOutputFile() + "was a directory and not a file!");
				}
				if (!fd.getOutputFile().delete()) {
					throw new MojoExecutionException("It was impossible to drop the already existing output file " + fd.getOutputFile());
				}
			}
			if (!fd.getInputFile().renameTo(fd.getOutputFile())) {
				if (!fd.isSilent()) {
					throw new MojoExecutionException("It was impossible to change the name of the file from " + fd.getInputFile() + " to "
							+ fd.getOutputFile());
				}
			}
		}
	}

}
