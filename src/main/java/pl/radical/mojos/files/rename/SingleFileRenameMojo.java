package pl.radical.mojos.files.rename;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * This plugin will change the names of the selected (single) files one by one.
 * <p>
 * It is designed to take a list of so called "jobs" and change all of the files name accordingly to the description of
 * the task. A <code>fileDefintion</code> have to be provided, although there can be more ten one in single plugin
 * execution.<br/>
 * A single entry can look as follows:
 * 
 * <pre>
 * &lt;filesDefinitions&gt;
 * 	 &lt;fileDefinition&gt;
 * 		&lt;inputFile&gt;/a/file/to/change/the/name&lt;/inputFile&gt;
 * 		&lt;outputFile&gt;/a/file/to/change/the/name&lt;/outputFile&gt;
 * 		&lt;silent&gt;true&lt;/silent&gt;
 * 		&lt;forceOverwrite&gt;true&lt;/forceOverwrite&gt;
 * 	 &lt;fileDefinition&gt;
 * &lt;/filesDefinitions&gt;
 * </pre>
 * 
 * <dl>
 * <dt>inputFile</dt>
 * <dd>A pathname to the file to which the change should be applied</dd>
 * <dt>outputFile</dt>
 * <dd>A target pathname for the change</dd>
 * <dt>forceOverwrite</dt>
 * <dd>Set to <code>true</code> if the plugin should first try to delete the target file if it exists,
 * <code>false</code> (default) otherwise</dd>
 * <dt>silent</dt>
 * <dd>Should not complain if the change was not successfull, default to false</dd>
 * </dl>
 * <p>
 * Keep in mind that both <code>silent</code> and <code>forceOverwrite</code> will actually override the settings of the
 * plugin itself, so the local job definition have a precedence over the plugin configuration.
 * <p>
 * Additionaly the plugin can be used to copy the content while still changing the name.
 * 
 * @goal rename-file
 * @phase process-resources
 */
public class SingleFileRenameMojo extends BaseFileReplacerMojo {

	/**
	 * A set of jobs definitions for this plugin to use.
	 * 
	 * @parameter
	 * @required
	 */
	protected Set<FileDefinition> filesDefinitions;

	public void execute() throws MojoExecutionException, MojoFailureException {
		File input, output;
		boolean silent, overwrite;
		for (final FileDefinition fd : filesDefinitions) {
			input = fd.getInputFile();
			output = fd.getOutputFile();

			silent = fd.isSilent() == null ? this.silent : fd.isSilent();
			overwrite = fd.isForceOverwrite() == null ? forceOverwrite : fd.isForceOverwrite();

			if (getLog().isDebugEnabled()) {
				getLog().debug(String.format("Preparing to rename the file: from \"%s\" to \"%s\" [silent: %b, forceOverwrite: %b]", fd
						.getInputFile(), fd.getOutputFile(), silent, overwrite));
			}

			if (output.exists() && overwrite) {
				// First check if it's not a directory by accident
				if (output.isDirectory()) {
					throw new MojoExecutionException("The given outpufile " + fd.getOutputFile() + "was a directory and not a file!");
				}
				// Then try to remove and complain if fails
				if (!output.delete()) {
					throw new MojoExecutionException("It was impossible to delete the already existing output file " + fd.getOutputFile());
				}
			}

			if (!input.renameTo(output)) {
				if (!silent) {
					throw new MojoExecutionException("It was impossible to change the name of the file from " + input + " to " + output);
				}
			} else {
				getLog().info(String.format("File renamed from \"%s\" to \"%s\"", fd.getInputFile(), fd.getOutputFile()));
			}
		}
	}

}
