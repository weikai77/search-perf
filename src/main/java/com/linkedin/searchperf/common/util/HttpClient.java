package com.linkedin.searchperf.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpClient {
  protected static Log LOG = LogFactory.getLog(HttpClient.class);

  public String postJson(String url, String jsonData) {
    
    return sendPostRaw(url, jsonData);
  }

  byte[] drain(InputStream inputStream) throws IOException {
    try {
      byte[] buf = new byte[1024];
      int len;
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      while ((len = inputStream.read(buf)) > 0) {
        byteArrayOutputStream.write(buf, 0, len);
      }
      return byteArrayOutputStream.toByteArray();
    } finally {
      inputStream.close();
    }
  }

  public String sendPostRaw(String urlStr, String requestStr) {
    return sendPostRaw(urlStr, requestStr, null);
  }
  public String sendGet(String urlStr) {
    return sendPostRaw(urlStr, null, null);
  }
  public String sendPostRaw(String urlStr, String requestStr, Map<String, String> headers) {
    HttpURLConnection conn = null;
    try {
      if (LOG.isInfoEnabled()) {
        LOG.info("Sending a post request to the server - " + urlStr);
      }      
      if (LOG.isDebugEnabled()) {
        LOG.debug("The request is - " + requestStr);
       
      }
      URL url = new URL(urlStr);
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      conn.setRequestProperty("Accept-Encoding", "gzip");
      conn.setRequestProperty("http.keepAlive", String.valueOf(true));
      conn.setRequestProperty("default", String.valueOf(true));
      if (headers != null && headers.size() > 0) {
        Set<Entry<String, String>> entries = headers.entrySet();
        for (Entry<String, String> entry : entries) {
          conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
      }
      if (requestStr != null) {
        byte[] requestBytes = requestStr.getBytes("UTF-8");
        conn.setRequestProperty("Content-Length", String.valueOf(requestBytes.length));
        conn.setDoOutput(true);
     
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      OutputStream os = new BufferedOutputStream(conn.getOutputStream());
      os.write(requestBytes);
      os.flush();
      os.close();
      } else {
        conn.setRequestMethod("GET");
      }
      int responseCode = conn.getResponseCode();
    
      if (LOG.isInfoEnabled()) {
        LOG.info("The http response code is " + responseCode);
      }
      if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
        throw new IOException("Failed : HTTP error code : " + responseCode);
      }
      byte[] bytes = drain(new BufferedInputStream(conn.getInputStream()));

      String output = new String(bytes, "UTF-8");
    /*  System.out.println("request = " + requestStr);
      System.out.println("response = " + output);*/
      if (LOG.isDebugEnabled()) {
        LOG.debug("The response from the server is - " + output);
      }
      return output;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      if (conn != null)
        conn.disconnect();
    }
  }
  
  public static void main(String[] args) {
    HttpClient httpClient = new HttpClient();
    
    System.out.println(httpClient.sendPostRaw("http://localhost:9200/cars/car/", "{id:3, groupdId:102}"));
  }
}
