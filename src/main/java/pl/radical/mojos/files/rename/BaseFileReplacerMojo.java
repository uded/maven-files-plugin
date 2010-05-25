package pl.radical.mojos.files.rename;

import org.apache.maven.plugin.AbstractMojo;

public abstract class BaseFileReplacerMojo extends AbstractMojo {

	/**
	 * @parameter default-value=false
	 */
	protected boolean silent;

	/**
	 * @parameter default-value=false
	 */
	protected boolean forceOverwrite;
}
