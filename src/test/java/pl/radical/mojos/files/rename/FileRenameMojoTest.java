package pl.radical.mojos.files.rename;

import static org.junit.Assert.assertTrue;

import pl.radical.mojos.tests.utils.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class FileRenameMojoTest {

	// FIXME Should be @Test
	public void testSingleFileRename() throws MojoExecutionException, MojoFailureException {
		final File output = new File("target/config.xml.rename");

		final FileDefinition fd = new FileDefinition();
		fd.setInputFile(new File("src/test/data/config.xml.tmpl"));
		fd.setOutputFile(output);

		final Set<FileDefinition> fds = new HashSet<FileDefinition>(1);
		fds.add(fd);

		final SingleFileRenameMojo mojo = new SingleFileRenameMojo();
		mojo.setLog(new Logger());
		mojo.filesDefinitions = fds;
		mojo.silent = false;
		mojo.forceOverwrite = false;

		mojo.execute();

		assertTrue(output.exists());
	}
}
