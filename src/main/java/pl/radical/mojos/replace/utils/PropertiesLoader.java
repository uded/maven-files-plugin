package pl.radical.mojos.replace.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author <a href="mailto:lukasz@radical.com.pl">Łukasz Rżanek</a>
 * @author Radical Creations &copy;2010
 */
public class PropertiesLoader {
	private final Map<String, String> tokens = new HashMap<String, String>();

	private final Set<String> delimiters;

	public PropertiesLoader(final Set<String> delimiters) {
		this.delimiters = delimiters;
	}

	public final void loadProperties(final String propertiesFile) throws MojoExecutionException {
		final File file = new File(propertiesFile);

		if (file.canRead()) {
			loadProperties(file);
		} else {
			if (!file.exists()) {
				throw new MojoExecutionException(String.format("The file [%s] was not found!", file.getAbsolutePath()));
			}
			if (!file.canRead()) {
				throw new MojoExecutionException(String.format("The file [%s] is not readable!", file.getAbsolutePath()));
			}
		}
	}

	public final void loadProperties(final File propertiesFile) throws MojoExecutionException {
		final Properties props = new Properties();
		AutoCloseInputStream fis = null;

		try {
			fis = new AutoCloseInputStream(new FileInputStream(propertiesFile));
			props.load(fis);
			loadProperties(props);
		} catch (final FileNotFoundException e) {
			throw new MojoExecutionException(String.format("A property file [%s] was not found", propertiesFile), e);
		} catch (final IOException e) {
			throw new MojoExecutionException(String.format("A property file [%s] cannot be read", propertiesFile), e);
		}
	}

	public final void loadProperties(final Properties properties) throws IOException {
		for (final Entry<Object, Object> entry : properties.entrySet()) {
			tokens.put(entry.getKey().toString().trim(), entry.getValue().toString().trim());
		}

		replaceTokens();
	}

	private void replaceTokens() {
		for (final String delimiter : delimiters) {
			final String[] delims = delimiter.split("\\|");
			for (final String key : tokens.keySet()) {
				final String oldValue = tokens.get(key);
				final String newValue = StrSubstitutor.replace(oldValue, tokens, delims[0], delims[1]);
				if (!oldValue.equals(newValue)) {
					tokens.put(key, newValue);
				}
			}
		}
	}

	public final Map<String, String> getTokens() {
		return tokens;
	}

	public final void writeToFile(final File file) throws IOException {
		final StringBuilder sb = new StringBuilder();
		final Properties props = new Properties();
		props.putAll(tokens);

		for (final Entry<String, String> entry : tokens.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append(System.getProperty("line.separator"));
		}

		FileUtils.writeStringToFile(file, sb.toString());
	}
}
