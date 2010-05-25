package pl.radical.mojos.files.rename;

import java.io.File;

public class FileDefinition {
	private File inputFile;

	private File outputFile;

	private Boolean silent;

	private Boolean forceOverwrite;

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(final File inputFile) {
		this.inputFile = inputFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(final File outputFile) {
		this.outputFile = outputFile;
	}

	public Boolean isSilent() {
		return silent;
	}

	public void setSilent(final boolean silent) {
		this.silent = silent;
	}

	public Boolean isForceOverwrite() {
		return forceOverwrite;
	}

	public void setForceOverwrite(final boolean forceOverwrite) {
		this.forceOverwrite = forceOverwrite;
	}

}
