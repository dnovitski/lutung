/**
 *
 */
package com.microtripit.mandrillapp.lutung.model;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError.MandrillError;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

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
	 * See https://hc.apache.org/httpcomponents-core-4.3.x/httpcore/apidocs/org/apache/http/params/HttpConnectionParams.html#setSoTimeout(org.apache.http.params.HttpParams, int)
	 *
	 * A value of 0 means no timeout at all.
	 * The value is expressed in milliseconds.
	 * */
	public static String SOCKET_TIMEOUT_MILLIS = "mandrill.socket.timeout";
	public static String SOCKET_TIMEOUT_MILLIS_DEFAULT = "5000";

	/**
	 * See https://hc.apache.org/httpcomponents-core-4.3.x/httpcore/apidocs/org/apache/http/params/HttpConnectionParams.html#setConnectionTimeout(org.apache.http.params.HttpParams, int)
	 *
	 * A value of 0 means no timeout at all.
	 * The value is expressed in milliseconds.
	 * */
	public static String CONNECTION_TIMEOUT_MILLIS = "mandrill.connection.timeout";
	public static String CONNECTION_TIMEOUT_MILLIS_DEFAULT = "5000";

	public static String LINGER_TIMEOUT_MILLIS = "mandrill.socket.linger.timeout";
	public static String LINGER_TIMEOUT_MILLIS_DEFAULT = "0";

	private static CloseableHttpClient httpClient;
	private static PoolingHttpClientConnectionManager connexionManager;
	private static RequestConfig defaultRequestConfig;

	private static int getSystemProperty(String name, String defaultValue) {
		String value = System.getProperty(name, defaultValue);
		return Integer.parseInt(value);
	}

	static {
		connexionManager = new PoolingHttpClientConnectionManager();
		connexionManager.setDefaultSocketConfig(SocketConfig.copy(SocketConfig.DEFAULT)
				.setSoLinger(getSystemProperty(LINGER_TIMEOUT_MILLIS, LINGER_TIMEOUT_MILLIS_DEFAULT))
				.setSoTimeout(getSystemProperty(SOCKET_TIMEOUT_MILLIS, SOCKET_TIMEOUT_MILLIS_DEFAULT))
				.build());
		connexionManager.setDefaultMaxPerRoute(50);
		defaultRequestConfig = RequestConfig.custom()
				.setSocketTimeout(getSystemProperty(SOCKET_TIMEOUT_MILLIS, SOCKET_TIMEOUT_MILLIS_DEFAULT))
				.setConnectTimeout(getSystemProperty(CONNECTION_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MILLIS_DEFAULT))
				.setConnectionRequestTimeout(getSystemProperty(CONNECTION_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MILLIS_DEFAULT)).build();
		httpClient = HttpClients.custom().setUserAgent("/Lutung-0.1")
				.setDefaultRequestConfig(defaultRequestConfig)
				.setConnectionManager(connexionManager).useSystemProperties()
				.build();
	}

	public static <T> T execute(final RequestModel<T> requestModel) throws MandrillApiError, IOException {

		HttpResponse response = null;
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
			response = httpClient.execute( requestModel.getRequest() );
			final StatusLine status = response.getStatusLine();
			responseString = EntityUtils.toString(response.getEntity());
			if( requestModel.validateResponseStatus(status.getStatusCode()) ) {
				try {
					return requestModel.handleResponse( responseString );

				} catch(final HandleResponseException e) {
					throw new IOException(
							"Failed to parse response from request '"
							+requestModel.getUrl()+ "'", e);

				}

			} else {
				// ==> compile mandrill error!
				MandrillError error = null;
				try {
				    error = LutungGsonUtils.getGson()
						.fromJson(responseString, MandrillError.class);
				} catch (Throwable ex) {
				    error = new MandrillError("Invalid Error Format",
				                              "Invalid Error Format",
				                              responseString,
				                              status.getStatusCode());
				}

				throw new MandrillApiError(
						"Unexpected http status in response: "
						+status.getStatusCode()+ " ("
						+status.getReasonPhrase()+ ")").withError(error);

			}

		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				log.error("Error consuming entity", e);
				throw e;
			}
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
