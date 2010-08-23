package pl.radical.mojos.files.replace;

public class RegExpression {
	private String expression;
	private String replacement;

	public RegExpression(final String expression, final String replacement) {
		this.expression = expression;
		this.replacement = replacement;
	}

	public final String getExpression() {
		return expression;
	}

	public final void setExpression(final String expression) {
		this.expression = expression;
	}

	public final String getReplacement() {
		return replacement;
	}

	public final void setReplacement(final String replacement) {
		this.replacement = replacement;
	}
}
