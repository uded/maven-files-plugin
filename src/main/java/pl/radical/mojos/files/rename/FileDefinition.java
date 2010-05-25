package pl.radical.mojos.files.rename;

import java.io.File;

public class FileDefinition {
	private File inputFile;

	private File outputFile;

	private boolean silent;
	
	private boolean forceOverwrite;

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public boolean isForceOverwrite() {
		return forceOverwrite;
	}

	public void setForceOverwrite(boolean forceOverwrite) {
		this.forceOverwrite = forceOverwrite;
	}

}
