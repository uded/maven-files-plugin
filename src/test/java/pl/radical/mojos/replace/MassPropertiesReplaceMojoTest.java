package pl.radical.mojos.replace;

import static org.junit.Assert.assertEquals;

import pl.radical.mojos.replace.utils.FileRenameRegexp;
import pl.radical.mojos.replace.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class MassPropertiesReplaceMojoTest {
	@Test(expected = MojoExecutionException.class)
	public void testBadDelimiter() throws MojoExecutionException {
		final Set<String> delimiters = new HashSet<String>();
		delimiters.add("${-}");

		final AbstractMassReplacerMojo mojo = new MassPropertiesReplaceMojo();
		mojo.delimiters = delimiters;
		mojo.getDelimiters();
	}

	@Test
	public void testDefaultDelimiters() throws MojoExecutionException {
		final AbstractMassReplacerMojo mojo = new MassPropertiesReplaceMojo();
		final Set<String> delimiters = mojo.getDelimiters();

		assertEquals(2, delimiters.size());
		assertEquals(true, delimiters.contains("@|@"));
		assertEquals(true, delimiters.contains("${|}"));
	}

	@Test
	public void testReplace() throws MojoExecutionException, MojoFailureException, IOException {
		// Files that will be tested
		final File fileOutput1 = new File("target/test.txt");
		final File fileResult1 = new File("src/test/data/test.txt.result");
		final File fileOutput2 = new File("target/config.xml");
		final File fileResult2 = new File("src/test/data/config.xml.result");

		// List of filters
		final List<String> filters = new ArrayList<String>(2);
		filters.add("src/test/data/001_test.properties");
		filters.add("src/test/data/002_test.properties");
		filters.add("src/test/data/003_test.properties");

		final FileRenameRegexp frr = new FileRenameRegexp();
		frr.setPattern("\\.tmpl$");
		frr.setReplace("");

		// Actuall work
		final AbstractMassReplacerMojo mojo = new MassPropertiesReplaceMojo();
		mojo.setLog(new Logger());

		mojo.delimiters = getDelimitersList();
		mojo.resources = getResources();
		mojo.excludeDefaults = true;
		mojo.filters = filters;
		mojo.fileRenameRegexp = frr;

		final Map<File, File> files = mojo.getFileList();
		assertEquals(2, files.size());

		mojo.execute();
		assertEquals(true, fileOutput2.exists());

		assertEquals(FileUtils.readFileToString(fileResult1), FileUtils.readFileToString(fileOutput1));
		assertEquals(FileUtils.readFileToString(fileResult2), FileUtils.readFileToString(fileOutput2));
	}

	private Resource[] getResources() {
		final Resource[] resources = new Resource[1];

		final List<String> includes = new ArrayList<String>(1);
		includes.add("**/*");

		final List<String> excludes = new ArrayList<String>(2);
		excludes.add("**/*.properties");
		excludes.add("**/*.result");

		// Resource entry
		final Resource resource = new Resource();
		resource.setDirectory("src/test/data");
		resource.setTargetPath("target");
		resource.setExcludes(excludes);
		resource.setIncludes(includes);

		resources[0] = resource;

		return resources;
	}

	/**
	 * Preparing delimiters list
	 * 
	 * @return delimiters list
	 */
	private Set<String> getDelimitersList() {
		final Set<String> delimiters = new HashSet<String>();
		delimiters.add("${|}");
		delimiters.add("@|@");
		delimiters.add("@@|@@");
		return delimiters;
	}
}
