package br.com.caelum.vraptor.serialization.gson.interceptor;

/**
 * This class holds the logic to determine which fields to include or exclude.
 */
public abstract class JsonFields {

	private String[] excludes;

	public abstract String[] includes();

	public String[] excludes() {
		return excludes;
	}

	public void setExcluded(final String[] fields) {
		this.excludes = fields;
	}

}
