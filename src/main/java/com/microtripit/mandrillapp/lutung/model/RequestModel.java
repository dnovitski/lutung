/**
 *
 */
package com.microtripit.mandrillapp.lutung.model;

import java.io.IOException;

import org.apache.hc.core5.http.ClassicHttpRequest;

/**
 *
 * @author rschreijer
 * @since Jan 7, 2013
 * @param <V> The type that response-data/ response-content is parsed to.
 */
public interface RequestModel<V> {

	/**
	 * @return The url for this request, as {@link String}.
	 */
	String getUrl();

	/**
	 * @return The request object describing the request to
	 * be made w/ an http client.
	 * @throws IOException IO Error
	 * @since Mar 22, 2013
	 */
	ClassicHttpRequest getRequest() throws IOException;

	/**
	 * <p>Checks weather the response-status is as-expected
	 * for this request.</p>
	 * @param httpResponseStatus The HTTP response status
	 * @return <code>true</code> if the response status is as expected,
	 * <code>false</code> otherwise.
	 */
	boolean validateResponseStatus(int httpResponseStatus);

	/**
	 * <p>Parses the content/data of this request's response into
	 * a desired format {@link V}.
	 * @param responseString The response as a string
	 * @return The response
	 * @throws HandleResponseException If an error occurs parsing the response.
	 */
	V handleResponse(String responseString) throws HandleResponseException;

}
