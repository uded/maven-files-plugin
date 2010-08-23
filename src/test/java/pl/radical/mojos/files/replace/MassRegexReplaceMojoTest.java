package pl.radical.mojos.files.replace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class MassRegexReplaceMojoTest {
	@Test
	public void testCorrectPattern() throws MojoExecutionException {
		final RegExpression[] re = new RegExpression[1];
		re[0] = new RegExpression("\\.*", "all");

		final MassRegexReplaceMojo mojo = new MassRegexReplaceMojo();
		mojo.regExps = re;
		mojo.readRegexps();
	}

	@Test(expected = MojoExecutionException.class)
	public void testInCorrectPattern() throws MojoExecutionException {
		final RegExpression[] re = new RegExpression[1];
		re[0] = new RegExpression("(\\.*))?", "all");

		final MassRegexReplaceMojo mojo = new MassRegexReplaceMojo();
		mojo.regExps = re;
		mojo.readRegexps();
	}

	@Test
	public void testReplace() throws MojoExecutionException, MojoFailureException, IOException {
		// Files that will be tested
		final File fileOutput1 = new File("target/test_regex.txt");
		final File fileResult1 = new File("src/test/data/test_regex.txt.result");

		final RegExpression[] re = new RegExpression[2];
		re[0] = new RegExpression("\\<\\!\\-\\-.*\\-\\-\\>", "");
		re[1] = new RegExpression("inregular expression", "Regular Expression");

		final MassRegexReplaceMojo mojo = new MassRegexReplaceMojo();
		mojo.regExps = re;
		mojo.excludeDefaults = true;
		mojo.resources = getResources();

		final Map<File, File> files = mojo.getFileList();
		assertEquals(3, files.size());

		mojo.execute();
		assertEquals(true, fileOutput1.exists());

		assertEquals(FileUtils.readFileToString(fileResult1), FileUtils.readFileToString(fileOutput1));
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

}
