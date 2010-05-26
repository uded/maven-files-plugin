package pl.radical.mojos.replace.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class PropertiesLoaderTest {

	@Test
	public void testPropertiesLoader() throws MojoExecutionException {
		final Set<String> delimiters = new HashSet<String>();
		delimiters.add("${|}");
		delimiters.add("@|@");
		delimiters.add("@@|@@");

		final PropertiesLoader propertiesLoader = new PropertiesLoader(delimiters);

		propertiesLoader.loadProperties("src/test/data/001_test.properties");
		propertiesLoader.loadProperties("src/test/data/002_test.properties");
		propertiesLoader.loadProperties("src/test/data/003_test.properties");

		// 001_test
		assertEquals("value1", propertiesLoader.getTokens().get("token1"));
		assertEquals("value2", propertiesLoader.getTokens().get("token2"));
		assertEquals("replaced-value1_value5", propertiesLoader.getTokens().get("complex.token"));

		// 002_test
		assertEquals("replaced-value1", propertiesLoader.getTokens().get("token3"));
		assertEquals("replaced-value2", propertiesLoader.getTokens().get("token4"));

		// 003_test
		assertEquals("value5", propertiesLoader.getTokens().get("token5"));
		assertEquals("value1_value2", propertiesLoader.getTokens().get("token6"));
		assertEquals("value1-delimitered", propertiesLoader.getTokens().get("token7"));
	}

	@Test(expected = MojoExecutionException.class)
	public void testStringWrongPropertiesLoader() throws MojoExecutionException {
		final Set<String> delimiters = new HashSet<String>();
		delimiters.add("${|}");
		delimiters.add("@|@");
		delimiters.add("@@|@@");

		final PropertiesLoader propertiesLoader = new PropertiesLoader(delimiters);

		propertiesLoader.loadProperties("src/test/data/wrong_test.properties");
	}

	@Test(expected = MojoExecutionException.class)
	public void testFileWrongPropertiesLoader() throws MojoExecutionException {
		final Set<String> delimiters = new HashSet<String>();
		delimiters.add("${|}");
		delimiters.add("@|@");
		delimiters.add("@@|@@");

		final PropertiesLoader propertiesLoader = new PropertiesLoader(delimiters);

		propertiesLoader.loadProperties(new File("src/test/data/wrong_test.properties"));
	}
}
