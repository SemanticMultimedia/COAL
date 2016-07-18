package org.s16a.mcas.util.http;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.s16a.mcas.util.musicbrainz.MusicBrainz;

public class HTTPUtil {

   private final static String PROPERTIES = "/musicbrainz.properties";

   public static class Response {
       public String response;
       public int responseCode;
   }

   public static Response get(String url) throws ClientProtocolException, IOException {
      final Properties properties = new Properties();

      properties.load(MusicBrainz.class.getResourceAsStream(PROPERTIES));

      final CloseableHttpClient httpclient = HttpClients.createDefault();
      final HttpGet httpGet = new HttpGet(url);

      httpGet.setHeader("User-Agent", properties.getProperty("useragent"));

      final CloseableHttpResponse httpResponse = httpclient.execute(httpGet);

      try {
         final HttpEntity httpEntity = httpResponse.getEntity();
         final Response response = new Response();

         response.response = EntityUtils.toString(httpEntity);
         response.responseCode = httpResponse.getStatusLine().getStatusCode();

         return response;
      } finally {
         httpResponse.close();
      }
   }
}
