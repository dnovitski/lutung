/**
 *
 */
package com.microtripit.mandrillapp.lutung.model;

/**
 * <p>Simple Wrapper class that is a container for a
 * <code>name,content</code> pair, as String objects.</p>
 * @author rschreijer
 * @since Mar 18, 2013
 */
public final class MandrillContentWrapper {
	private String name, content;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	public static MandrillContentWrapper create(
			final String name, final String content) {

		final MandrillContentWrapper w = new MandrillContentWrapper();
		w.setName(name);
		w.setContent(content);
		return w;

	}

}
