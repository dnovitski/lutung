/**
 *
 */
package com.microtripit.mandrillapp.lutung.model;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError.MandrillError;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;

/**
 * @author rschreijer
 * @since Feb 21, 2013
 */
@Slf4j
public final class MandrillRequestDispatcher {

	/**
	 * See https://hc.apache.org/httpcomponents-core-5.3.x/current/httpcore5/apidocs/org/apache/hc/core5/http/config/SocketConfig.Builder.html#setSoTimeout(org.apache.hc.core5.util.Timeout)
	 *
	 * A value of 0 means no timeout at all.
	 * The value is expressed in milliseconds.
	 * */
	public static String SOCKET_TIMEOUT_MILLIS = "mandrill.socket.timeout";
	public static String SOCKET_TIMEOUT_MILLIS_DEFAULT = "5000";

	/**
	 * See https://hc.apache.org/httpcomponents-client-5.3.x/current/httpclient5/apidocs/org/apache/hc/client5/http/config/RequestConfig.Builder.html#setConnectTimeout(org.apache.hc.core5.util.Timeout)
	 *
	 * A value of 0 means no timeout at all.
	 * The value is expressed in milliseconds.
	 * */
	public static String CONNECTION_TIMEOUT_MILLIS = "mandrill.connection.timeout";
	public static String CONNECTION_TIMEOUT_MILLIS_DEFAULT = "5000";

	public static String LINGER_TIMEOUT_MILLIS = "mandrill.socket.linger.timeout";
	public static String LINGER_TIMEOUT_MILLIS_DEFAULT = "0";

	private static CloseableHttpClient httpClient;
	private static final PoolingHttpClientConnectionManager connexionManager;
	private static final RequestConfig defaultRequestConfig;

	private static int getSystemProperty(String name, String defaultValue) {
		String value = System.getProperty(name, defaultValue);
		return Integer.parseInt(value);
	}

	static {
		connexionManager = new PoolingHttpClientConnectionManager();
		connexionManager.setDefaultSocketConfig(SocketConfig.copy(SocketConfig.DEFAULT)
				.setSoLinger(TimeValue.ofMilliseconds(getSystemProperty(LINGER_TIMEOUT_MILLIS, LINGER_TIMEOUT_MILLIS_DEFAULT)))
				.setSoTimeout(Timeout.ofMilliseconds(getSystemProperty(SOCKET_TIMEOUT_MILLIS, SOCKET_TIMEOUT_MILLIS_DEFAULT)))
				.build());
		connexionManager.setDefaultMaxPerRoute(50);
		defaultRequestConfig = RequestConfig.custom()
				.setResponseTimeout(Timeout.ofMilliseconds(getSystemProperty(SOCKET_TIMEOUT_MILLIS, SOCKET_TIMEOUT_MILLIS_DEFAULT)))
				.setConnectTimeout(Timeout.ofMilliseconds(getSystemProperty(CONNECTION_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MILLIS_DEFAULT)))
				.setConnectionRequestTimeout(Timeout.ofMilliseconds(getSystemProperty(CONNECTION_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MILLIS_DEFAULT))).build();
		httpClient = HttpClients.custom().setUserAgent("/Lutung-0.1")
				.setDefaultRequestConfig(defaultRequestConfig)
				.setConnectionManager(connexionManager).useSystemProperties()
				.build();
	}

	public static <T> T execute(final RequestModel<T> requestModel) throws MandrillApiError, IOException {

		String responseString = null;
		try {
			// use proxy?
			final ProxyData proxyData = detectProxyServer(requestModel.getUrl());
			if (proxyData != null) {
				log.debug("Using proxy @{}:{}", proxyData.host, proxyData.port);
				final HttpHost proxy = new HttpHost(proxyData.host, proxyData.port);

				RequestConfig requestConfig = RequestConfig.custom()
						.setProxy(proxy).build();

				httpClient = HttpClients.custom().setUserAgent("/Lutung-0.1")
						.setDefaultRequestConfig(requestConfig)
						.setConnectionManager(connexionManager).useSystemProperties()
						.build();
			}
			log.debug("starting request '{}'", requestModel.getUrl());
			try (CloseableHttpResponse response = httpClient.execute(requestModel.getRequest())) {
				final int statusCode = response.getCode();
				responseString = EntityUtils.toString(response.getEntity());
				if( requestModel.validateResponseStatus(statusCode) ) {
				try {
					return requestModel.handleResponse(responseString);

				} catch(final HandleResponseException e) {
					throw new IOException(
							"Failed to parse response from request '"
							+requestModel.getUrl()+ "'", e);

				}

				} else {
					// ==> compile mandrill error!
					MandrillError error;
					try {
					    error = LutungGsonUtils.getGson()
							.fromJson(responseString, MandrillError.class);
					} catch (Throwable ex) {
					    error = new MandrillError("Invalid Error Format",
					                              "Invalid Error Format",
					                              responseString,
					                              statusCode);
					}

					throw new MandrillApiError(
							"Unexpected http status in response: "
							+statusCode+ " ("
							+response.getReasonPhrase()+ ")").withError(error);

				}
			}
		} catch (ParseException e) {
			throw new IOException("Unable to parse HTTP response for request '" + requestModel.getUrl() + "'", e);
		}
	}

    private static ProxyData detectProxyServer(final String url) {
        try {
            final List<Proxy> proxies = ProxySelector.getDefault().select(new URI(url));
            if(proxies != null) {
                for(Proxy proxy : proxies) {
                    InetSocketAddress addr = (InetSocketAddress) proxy.address();
                    if(addr != null) {
                        return new ProxyData(addr.getHostName(), addr.getPort());
                    }
                }
            }
            // no proxy detected!
            return null;

        } catch (final Throwable t) {
            log.error("Error detecting proxy server", t);
            return null;

        }
    }

    private static final class ProxyData {
        String host;
        int port;

        protected ProxyData(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

    }

}
