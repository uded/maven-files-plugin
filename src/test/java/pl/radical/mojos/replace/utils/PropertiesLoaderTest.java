package pl.radical.mojos.replace.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class PropertiesLoaderTest {

	@Test
	public void testPropertiesReplacer() throws MojoExecutionException {
		final Set<String> delimiters = new HashSet<String>();
		delimiters.add("${|}");
		delimiters.add("@|@");
		delimiters.add("@@|@@");

		final PropertiesLoader propertiesLoader = new PropertiesLoader(delimiters);

		propertiesLoader.loadProperties("src/test/data/001_test.properties");
		propertiesLoader.loadProperties("src/test/data/002_test.properties");
		propertiesLoader.loadProperties("src/test/data/003_test.properties");

		assertEquals("value1", propertiesLoader.getTokens().get("token1"));
		assertEquals("value2", propertiesLoader.getTokens().get("token2"));
		assertEquals("replaced-value1", propertiesLoader.getTokens().get("token3"));
		assertEquals("replaced-value2", propertiesLoader.getTokens().get("token4"));
		assertEquals("replaced-value1_value5", propertiesLoader.getTokens().get("complex.token"));
	}
}