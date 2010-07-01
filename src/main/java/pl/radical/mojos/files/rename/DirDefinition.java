package pl.radical.mojos.files.rename;

import java.io.File;

public class DirDefinition {
	private File inputDir;

	private File outputDir;

	private Boolean silent;

	private Boolean forceOverwrite;

	public final File getInputDir() {
		return inputDir;
	}

	public final void setInputDir(final File inputDir) {
		this.inputDir = inputDir;
	}

	public final File getOutputDir() {
		return outputDir;
	}

	public final void setOutputDir(final File outputDir) {
		this.outputDir = outputDir;
	}

	public final Boolean isSilent() {
		return silent;
	}

	public final void setSilent(final Boolean silent) {
		this.silent = silent;
	}

	public final Boolean isForceOverwrite() {
		return forceOverwrite;
	}

	public final void setForceOverwrite(final Boolean forceOverwrite) {
		this.forceOverwrite = forceOverwrite;
	}

}
