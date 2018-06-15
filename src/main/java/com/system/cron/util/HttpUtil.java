package com.system.cron.util;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http Client 工具封装
 */
public class HttpUtil
{

    private static final String HTTP_PROTOCOL = "http";

    private static final String HTTPS_PROTOCOL = "https";

    private static final String[] SUPPORTED_PROTOCOLS =
        new String[] {"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"};

    private static final int MAX_TOTAL = 500;

    private static final int DEFAULT_MAX_PER_ROUTE = 50;

    private static final int CONNECT_TIMEOUT = 3000; //请求连接超时时间，默认3秒

    private static final int SOCKET_TIMEOUT = 3000; //连接建立后，返回结果的超时时间，默认3秒

    private static final long KEEP_ALIVE_DURATION = 5000; // 长连接时间，默认5秒

    private static final int RETRY_COUNT = 0; // 重试次数，设为0，不设置默认3次

    private static final boolean SET_TIMEOUT = true; // 默认设置超时时间

    private static final String USER_AGENT = "Mozilla/5.0";

    private static CloseableHttpClient httpClient;

    static
    {
        // 下述ssl连接配置也能用，但此处不选择
//        X509TrustManager tm = new X509TrustManager() {
//            @Override
//            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//            }
//            @Override
//            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//            }
//            @Override
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//        };
//        SSLContext sslContext = null;
//        try {
//            sslContext = SSLContext.getInstance("TLS");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        try {
//            sslContext.init(null, new TrustManager[]{tm}, null);
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
//        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.INSTANCE)
//                .register("https", sslsf)
//                .build();
        try
        {
            // ssl连接配置，测试发现一些国外的https地址无法建立连接，如https://yesno.wtf/api
            // 无服务器证书，采用自定义信任机制，全部信任，不做身份鉴定
            SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null,
                    (TrustStrategy)(chain, authType) -> true)
                .build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext,
                SUPPORTED_PROTOCOLS,
                null,
                NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP_PROTOCOL,
                        PlainConnectionSocketFactory.INSTANCE)
                    .register(HTTPS_PROTOCOL, sslsf)
                    .build();
            // 连接池
            PoolingHttpClientConnectionManager cm =
                new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(MAX_TOTAL);
            cm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
            //自定义连接保持存活策略:如果响应头中有Keep-Alive相关的timeout设置，则按Keep-Alive头中的timeout设定存活时间。不然默认为5秒。
            ConnectionKeepAliveStrategy ckas =
                new DefaultConnectionKeepAliveStrategy()
                {
                    @Override
                    public long getKeepAliveDuration(HttpResponse response,
                        HttpContext context)
                    {
                        long keepAlive =
                            super.getKeepAliveDuration(response, context);
                        if (keepAlive == -1)
                        {
                            keepAlive = KEEP_ALIVE_DURATION;
                        }
                        return keepAlive;
                    }
                };
            //设置默认的请求和传输超时时间
            RequestConfig rc;
            if (SET_TIMEOUT)
            {
                rc = RequestConfig.custom()
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .build();
            }
            // 重试策略，设为不重试，默认为3次
            HttpRequestRetryHandler hrrh =
                new DefaultHttpRequestRetryHandler(RETRY_COUNT, false);
            httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setKeepAliveStrategy(ckas)
                .setDefaultRequestConfig(rc)
                .setRetryHandler(hrrh)
                .build();
        }
        catch (Exception e)
        {
            System.out.println("HttpUtil CloseableHttpClient初始化失败");
            e.printStackTrace();
        }
    }

    public static CloseableHttpClient getHttpClient()
    {
        return httpClient;
    }

    /**
     * httpClient get请求，返回响应结果
     *
     * @param url    请求url
     * @param header 头部信息
     * @param param  请求参数，url查询字符串，没有传null
     * @return 响应结果，包含状态码和响应体
     * @throws IOException
     */
    public static Response get(String url, Map<String, String> header,
        Map<String, String> param)
        throws IOException
    {
        return get(url, header, param, true);
    }

    /**
     * httpClient get请求
     *
     * @param url          请求url
     * @param header       头部信息
     * @param param        请求参数，url查询字符串，没有传null
     * @param needResponse 是否需要返回响应体
     * @return 响应结果，包含状态码，响应体只在设置为需要返回时包含
     * @throws IOException
     */
    public static Response get(String url, Map<String, String> header,
        Map<String, String> param, boolean needResponse)
        throws IOException
    {
        Response result = null;
        StringBuilder sb = new StringBuilder(url);
        if (MapUtils.isNotEmpty(param))
        {
            boolean firstFlag = true;
            for (Map.Entry<String, String> entry : param.entrySet())
            {
                if (firstFlag)
                {
                    sb.append("?")
                        .append(URLEncoder.encode(entry.getKey(),
                            Consts.UTF_8.toString()))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(),
                            Consts.UTF_8.toString()));
                    firstFlag = false;
                }
                else
                {
                    sb.append("&")
                        .append(URLEncoder.encode(entry.getKey(),
                            Consts.UTF_8.toString()))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(),
                            Consts.UTF_8.toString()));
                }
            }
        }
        HttpGet httpGet = new HttpGet(sb.toString());
        httpGet.addHeader(HTTP.USER_AGENT, USER_AGENT);
        if (MapUtils.isNotEmpty(header))
        {
            for (Map.Entry<String, String> entry : header.entrySet())
            {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        CloseableHttpResponse response = null;
        try
        {
            response = httpClient.execute(httpGet); // 该方法为阻塞方法，会返回最终的结果
            if (needResponse)
            {
                result = extractResponse(response);
            }
            EntityUtils.consume(response.getEntity());
        }
        finally
        {
            try
            {
                if (null != response)
                    response.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * httpClient post请求，返回响应结果
     *
     * @param url       请求url
     * @param header    头部信息
     * @param param     请求参数，form提交适用
     * @param reqEntity 请求实体字符串，json提交适用，后期添加xml
     * @return 响应结果，包含状态码和响应体
     * @throws IOException
     */
    public static Response post(String url, Map<String, String> header,
        Map<String, String> param, String reqEntity)
        throws IOException
    {
        return post(url, header, param, reqEntity, true);
    }

    /**
     * httpClient post请求
     *
     * @param url          请求url
     * @param header       头部信息
     * @param param        请求参数，form提交适用
     * @param reqEntity    请求实体字符串，json提交适用，后期添加xml
     * @param needResponse 是否需要返回响应体
     * @return 响应结果，包含状态码，响应体只在设置为需要返回时包含
     * @throws IOException
     */
    public static Response post(String url, Map<String, String> header,
        Map<String, String> param, String reqEntity, boolean needResponse)
        throws IOException
    {
        Response result = null;
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.USER_AGENT, USER_AGENT);
        if (MapUtils.isNotEmpty(header))
        {
            for (Map.Entry<String, String> entry : header.entrySet())
            {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (MapUtils.isNotEmpty(param))
        {
            List<NameValuePair> formparams = new ArrayList<>();
            for (Map.Entry<String, String> entry : param.entrySet())
            {
                formparams.add(new BasicNameValuePair(entry.getKey(),
                    entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formparams,
                Consts.UTF_8));
        }
        // 设置json实体，优先级高，会覆盖前面的urlEncodedFormEntity
        if (StringUtils.isNotBlank(reqEntity))
        {
            httpPost.setEntity(new StringEntity(reqEntity,
                ContentType.APPLICATION_JSON));
        }
        CloseableHttpResponse response = null;
        try
        {
            response = httpClient.execute(httpPost);
            if (needResponse)
            {
                result = extractResponse(response);
            }
            EntityUtils.consume(response.getEntity());
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            try
            {
                if (null != response)
                    response.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String formatHttpResponse(HttpResponse httpResponse)
        throws ParseException, IOException
    {
        StringBuilder builder = new StringBuilder();
        HttpEntity entity = httpResponse.getEntity();
        builder.append(httpResponse.getStatusLine()).append("\n");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext())
        {
            builder.append(iterator.nextHeader()).append("\n");
        }
        builder.append("\n");
        if (entity != null && entity.getContent() != null)
        {
            builder.append(EntityUtils.toString(entity));
        }
        return builder.toString();
    }

    private static Response extractResponse(CloseableHttpResponse response)
        throws IOException
    {
        Response r = new Response();
        r.setStatusCode(response.getStatusLine().getStatusCode());
        r.setResponseBody(EntityUtils.toString(response.getEntity(),
            Consts.UTF_8));
        return r;
    }

    public static class Response
    {
        private Integer statusCode;

        private String responseBody;

        public Integer getStatusCode()
        {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode)
        {
            this.statusCode = statusCode;
        }

        public String getResponseBody()
        {
            return responseBody;
        }

        public void setResponseBody(String responseBody)
        {
            this.responseBody = responseBody;
        }

        @Override
        public String toString()
        {
            return "Response{" +
                "statusCode=" + statusCode +
                ", responseBody='" + responseBody + '\'' +
                '}';
        }
    }

}
