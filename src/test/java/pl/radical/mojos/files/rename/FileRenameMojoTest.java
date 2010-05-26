package pl.radical.mojos.files.rename;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class FileRenameMojoTest {

	@Test
	public void testSingleFileRename() throws MojoExecutionException, MojoFailureException {
		final File output = new File("target/config.xml.rename");
		//
		// final FileDefinition fd = new FileDefinition();
		// fd.setInputFile(new File("src/test/data/config.xml.tmpl"));
		// fd.setOutputFile(output);
		//
		// final Set<FileDefinition> fds = new HashSet<FileDefinition>(1);
		// fds.add(fd);
		//
		// final SingleFileRenameMojo mojo = new SingleFileRenameMojo();
		// mojo.setLog(new Logger());
		// mojo.filesDefinitions = fds;
		// mojo.silent = false;
		// mojo.forceOverwrite = false;
		//
		// mojo.execute();

		// assertTrue(output.exists());
	}
}
