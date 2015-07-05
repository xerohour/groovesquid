package com.groovesquid.service;

import com.groovesquid.Groovesquid;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpService {

    protected final static Logger log = Logger.getLogger(HttpService.class.getName());

    protected String userAgent = "Groovesquid/" + Groovesquid.getVersion() + " +http://groovesquid.com";
    protected String browserUserAgent;
    protected Header[] browserHeaders;
    protected HttpClient httpClient;

    public HttpService() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        if (Groovesquid.getConfig().getProxyHost() != null && Groovesquid.getConfig().getProxyPort() != null) {
            httpClientBuilder.setProxy(new HttpHost(Groovesquid.getConfig().getProxyHost(), Groovesquid.getConfig().getProxyPort()));
        }
        httpClient = httpClientBuilder.build();

        browserUserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
        List<Header> browserHeadersList = new ArrayList<Header>();
        browserHeadersList.add(new BasicHeader(HTTP.USER_AGENT, browserUserAgent));
        browserHeadersList.add(new BasicHeader("Content-Language", "en-US"));
        browserHeadersList.add(new BasicHeader("Cache-Control", "max-age=0"));
        browserHeadersList.add(new BasicHeader("Accept", "*/*"));
        browserHeadersList.add(new BasicHeader("Accept-Charset", "utf-8,ISO-8859-1;q=0.7,*;q=0.3"));
        browserHeadersList.add(new BasicHeader("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4"));
        browserHeadersList.add(new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"));
        browserHeaders = new Header[browserHeadersList.size()];
        browserHeaders = browserHeadersList.toArray(browserHeaders);
    }

    public String get(String url, List<Header> headers) {
        String responseContent = null;
        HttpEntity httpEntity = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HTTP.USER_AGENT, userAgent);
            if (headers != null) {
                Header[] headersArr = new Header[headers.size()];
                httpGet.setHeaders(headers.toArray(headersArr));
            }

            HttpResponse httpResponse = httpClient.execute(httpGet);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpEntity = httpResponse.getEntity();

            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                httpEntity.writeTo(baos);
            } else {
                throw new RuntimeException("status code: " + statusLine.getStatusCode());
            }

            responseContent = baos.toString("UTF-8");

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        return responseContent;
    }

    public byte[] getRaw(String url, List<Header> headers) {
        byte[] responseContent = null;
        HttpEntity httpEntity = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HTTP.USER_AGENT, userAgent);
            if (headers != null) {
                Header[] headersArr = new Header[headers.size()];
                httpGet.setHeaders(headers.toArray(headersArr));
            }

            HttpResponse httpResponse = httpClient.execute(httpGet);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpEntity = httpResponse.getEntity();

            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                httpEntity.writeTo(baos);
            } else {
                throw new RuntimeException("status code: " + statusLine.getStatusCode());
            }

            responseContent = baos.toByteArray();

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        return responseContent;
    }

    public byte[] getRaw(String url) {
        return getRaw(url, null);
    }

    public String get(String url) {
        return get(url, null);
    }

    public String post(String url, List<NameValuePair> data, List<Header> headers) {
        String responseContent = null;
        HttpEntity httpEntity = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HTTP.USER_AGENT, userAgent);
            if (headers != null) {
                Header[] headersArr = new Header[headers.size()];
                httpPost.setHeaders(headers.toArray(headersArr));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpEntity = httpResponse.getEntity();

            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                httpEntity.writeTo(baos);
            } else {
                throw new RuntimeException("status code: " + statusLine.getStatusCode());
            }

            responseContent = baos.toString("UTF-8");

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        return responseContent;
    }

    public String post(String url, String data, List<Header> headers) {
        String responseContent = null;
        HttpEntity httpEntity = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HTTP.USER_AGENT, userAgent);
            if (headers != null) {
                Header[] headersArr = new Header[headers.size()];
                httpPost.setHeaders(headers.toArray(headersArr));
            }
            httpPost.setEntity(new StringEntity(data));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpEntity = httpResponse.getEntity();

            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                httpEntity.writeTo(baos);
            } else {
                throw new RuntimeException("status code: " + statusLine.getStatusCode());
            }

            responseContent = baos.toString("UTF-8");

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
        return responseContent;
    }

    public String post(String url, List<NameValuePair> data) {
        return post(url, data, null);
    }

}
