package pl.radical.mojos.replace.utils;

import org.apache.maven.plugin.logging.SystemStreamLog;

public class Logger extends SystemStreamLog {

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

}
