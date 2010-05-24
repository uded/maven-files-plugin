package pl.radical.mojos.replace.utils;

public class FileRenameRegexp {
	private String pattern;
	private String replace;

	public final String getPattern() {
		return pattern;
	}

	public final void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	public final  String getReplace() {
		return replace;
	}

	public final void  setReplace(final String replace) {
		this.replace = replace;
	}

	@Override
	public final String toString() {
		return String.format("[%s] will be replace to [%s]", pattern, replace);
	}

}
