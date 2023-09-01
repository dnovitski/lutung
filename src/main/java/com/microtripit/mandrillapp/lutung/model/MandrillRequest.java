/**
 *
 */
package com.microtripit.mandrillapp.lutung.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import java.util.Map;

/**
 * @author rschreijer
 * @since Mar 16, 2013
 */
@Slf4j
public final class MandrillRequest<OUT> implements RequestModel<OUT> {

	private final String url;
	private final Class<OUT> responseContentType;
	private final Map<String,? extends Object> requestParams;

	public MandrillRequest( final String url,
			final Map<String,? extends Object> params,
			final Class<OUT> responseType ) {

		if(responseType == null) {
			throw new NullPointerException();

		}
		this.url = url;
		this.requestParams = params;
		this.responseContentType = responseType;
	}

	public String getUrl() {
		return url;
	}

	public HttpRequestBase getRequest() {
		final String paramsStr = LutungGsonUtils
			.getGson()
			.toJson(requestParams, requestParams.getClass());
        log.debug("raw content for request:\n" +paramsStr);
		final StringEntity entity = new StringEntity(paramsStr, "UTF-8");
		entity.setContentType("application/json");
		final HttpPost request = new HttpPost(url);
		request.setEntity(entity);
		return request;

	}

	public boolean validateResponseStatus(final int httpResponseStatus) {
		return (httpResponseStatus == 200);
	}

	public OUT handleResponse(final String responseString)
			throws HandleResponseException {
		try {
            log.debug("raw content from response: {}", responseString);
			return LutungGsonUtils.getGson().fromJson(responseString, responseContentType);
		} catch(final Throwable t) {
			String msg = "Error handling Mandrill response " +
				((responseString != null) ? ": '" + responseString + "'" : "");
			throw new HandleResponseException(msg, t);
		}
	}

}
