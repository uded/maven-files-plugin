package pl.radical.mojos.files.rename;

import java.io.File;

public class FileDefinition {
	private File inputFile;

	private File outputFile;

	private Boolean silent;

	private Boolean forceOverwrite;

	public final File getInputFile() {
		return inputFile;
	}

	public final void setInputFile(final File inputFile) {
		this.inputFile = inputFile;
	}

	public final File getOutputFile() {
		return outputFile;
	}

	public final void setOutputFile(final File outputFile) {
		this.outputFile = outputFile;
	}

	public final Boolean isSilent() {
		return silent;
	}

	public final void setSilent(final boolean silent) {
		this.silent = silent;
	}

	public final Boolean isForceOverwrite() {
		return forceOverwrite;
	}

	public final void setForceOverwrite(final boolean forceOverwrite) {
		this.forceOverwrite = forceOverwrite;
	}

}
