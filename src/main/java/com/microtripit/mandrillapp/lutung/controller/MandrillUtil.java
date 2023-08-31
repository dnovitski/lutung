/**
 *
 */
package com.microtripit.mandrillapp.lutung.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.model.MandrillRequest;
import com.microtripit.mandrillapp.lutung.model.MandrillRequestDispatcher;

/**
 * @author rschreijer
 * @since Mar 19, 2013
 */
final class MandrillUtil {
	/**
	 * @param key
	 * @return
	 */
	protected static HashMap<String,Object> paramsWithKey(final String key) {
		final HashMap<String,Object> params = new HashMap<>();
		params.put("key",key);
		return params;
	}

	/**
	 * @param url
	 * @param params
	 * @param responseType
	 * @return
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	protected static <OUT> OUT query(final String url,
									 final Map<String,Object> params, Class<OUT> responseType)
					throws MandrillApiError, IOException {

		final MandrillRequest<OUT> requestModel =
			new MandrillRequest<>(url, params, responseType);
		return MandrillRequestDispatcher.execute(requestModel);

	}
}
