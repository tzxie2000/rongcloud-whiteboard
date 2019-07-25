package com.blink.ewb.util;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blink.ewb.Config;

public class HttpAsyncClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpAsyncClientUtil.class);

	private static HttpAsyncClientBuilder httpAsyncClientBuilder;
	
	private static HttpAsyncClientBuilder httpAsyncClientBuilder_https;

	static {
		init();
	}

	private static void init() {
		try {
			// 创建HttpClientBuilder
			httpAsyncClientBuilder = HttpAsyncClientBuilder.create();
			httpAsyncClientBuilder_https = HttpAsyncClientBuilder.create();
			setTimeout(Config.connection_request_timeout, Config.connect_timeout, Config.socket_timeout);
//			setPool(Config.pool_max_total, Config.pool_default_max_per_route);
			setSSL();
		} catch (Exception e) {
			logger.error("error:", e);
		}
	}

	private static void setTimeout(int connectionRequestTimeout, int connectTimeout, int socketTimeout) {
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
				.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();

		httpAsyncClientBuilder.setDefaultRequestConfig(requestConfig);
		httpAsyncClientBuilder_https.setDefaultRequestConfig(requestConfig);
	}

//	private static void setPool(int maxTotal, int defaultMaxPerRoute) throws Exception {
//		// 配置io线程
//		ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
//		// 设置连接池大小
//		PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
//		connManager.setMaxTotal(maxTotal);
//		connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
//
//		httpAsyncClientBuilder.setConnectionManager(connManager);
//	}

	private static void setSSL() throws Exception {
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {

			@Override
			public boolean isTrusted(X509Certificate[] certificate, String authType) {
				return true;
			}

		};
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}

		};

		httpAsyncClientBuilder_https.setSSLStrategy(new SSLIOSessionStrategy(sslContext, hostnameVerifier));
	}

	private static CloseableHttpAsyncClient getHttpAsyncClient(String scheme) {
		if ("https".equals(scheme)) {
			return httpAsyncClientBuilder_https.build();
		} else {
			return httpAsyncClientBuilder.build();
		}
	}

	public static HttpGet getHttpGet(String url) {
		return new HttpGet(url);
	}

	public static void httpGet(String url) throws Exception {
		HttpGet httpGet = getHttpGet(url);
		execute(httpGet);
	}

	public static void httpGet(String url, IHandler handler) throws Exception {
		HttpGet httpGet = getHttpGet(url);
		execute(httpGet, handler);
	}

	public static HttpPost getHttpPost(String url) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		return httpPost;
	}

	public static HttpPost getHttpPost(String url, Map<String, String> paramsMap) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (String key : paramsMap.keySet()) {
			nameValuePairs.add(new BasicNameValuePair(key, paramsMap.get(key)));
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		return httpPost;
	}

	public static HttpPost getHttpPost(String url, String content) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		HttpEntity entity = new StringEntity(content, "UTF-8");
		httpPost.setEntity(entity);
		return httpPost;
	}

	public static void httpPost(String url) throws Exception {
		HttpPost httpPost = getHttpPost(url);
		execute(httpPost);
	}

	public static void httpPost(String url, IHandler handler) throws Exception {
		HttpPost httpPost = getHttpPost(url);
		execute(httpPost, handler);
	}

	public static void httpPost(String url, Map<String, String> paramsMap) throws Exception {
		HttpPost httpPost = getHttpPost(url, paramsMap);
		execute(httpPost);
	}

	public static void httpPost(String url, Map<String, String> paramsMap, IHandler handler) throws Exception {
		HttpPost httpPost = getHttpPost(url, paramsMap);
		execute(httpPost, handler);
	}

	public static void httpPost(String url, String content) throws Exception {
		HttpPost httpPost = getHttpPost(url, content);
		execute(httpPost);
	}

	public static void httpPost(String url, String content, IHandler handler) throws Exception {
		HttpPost httpPost = getHttpPost(url, content);
		execute(httpPost, handler);
	}

	public static void execute(HttpUriRequest request) throws Exception {
		IHandler handler = new IHandler() {

			@Override
			public void failed(Exception e) {
				logger.error("error:", e);
			}

			@Override
			public void completed(HttpResponse response) {
				try {
					HttpEntity entity = response.getEntity();
					String result = EntityUtils.toString(entity, "UTF-8");
					logger.info(result);
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}

			@Override
			public void cancelled() {

			}
		};

		execute(request, handler);
	}

	public static void execute(HttpUriRequest request, IHandler handler) throws Exception {
		String scheme = request.getURI().getScheme();
		CloseableHttpAsyncClient httpClient = getHttpAsyncClient(scheme);
		// Start the client
		httpClient.start();
		// 异步执行请求操作，通过回调，处理结果
		httpClient.execute(request, new FutureCallback<HttpResponse>() {

			@Override
			public void cancelled() {
				handler.cancelled();
				// close(httpClient);
			}

			@Override
			public void completed(HttpResponse response) {
				try {
					handler.completed(response);
				} finally {
					// close(response, httpClient);
					close(response);
				}
			}

			@Override
			public void failed(Exception e) {
				handler.failed(e);
				// close(httpClient);
			}
		});
	}

	public static void close(HttpResponse response, CloseableHttpAsyncClient httpClient) {
		// 关闭连接,释放资源
		close(response);
		close(httpClient);
	}

	public static void close(HttpResponse response) {
		try {
			if (response != null) {
				// 如果CloseableHttpResponse 是resp的父类，则支持关闭
				if (CloseableHttpResponse.class.isAssignableFrom(response.getClass())) {
					((CloseableHttpResponse) response).close();
				}
			}
		} catch (IOException e) {
			logger.error("error:", e);
		}
	}

	private static void close(CloseableHttpAsyncClient httpClient) {
		try {
			if (httpClient != null) {
				httpClient.close();
			}
		} catch (IOException e) {
			logger.error("error:", e);
		}
	}

	/**
	 * 回调处理接口
	 * 
	 */
	public interface IHandler {

		/**
		 * 处理异常时，执行该方法
		 */
		void failed(Exception e);

		/**
		 * 处理正常时，执行该方法
		 */
		void completed(HttpResponse response);

		/**
		 * 处理取消时，执行该方法
		 */
		void cancelled();
	}

}