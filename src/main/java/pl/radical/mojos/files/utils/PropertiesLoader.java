package pl.radical.mojos.files.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.text.StrSubstitutor;

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

	public final void loadProperties(final String propertiesFile) throws IOException {
		loadProperties(new File(propertiesFile));
	}

	public final void loadProperties(final File propertiesFile) throws IOException {
		final Properties props = new Properties();
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(propertiesFile);
			props.load(fis);
			loadProperties(props);
		} finally {
			if (fis != null) {
				fis.close();
			}
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

	public final void writeToFile(final File file) {
		final StringBuilder sb = new StringBuilder();
		for (final Entry<String, String> entry : tokens.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
		}
	}
}
