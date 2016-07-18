package org.s16a.mcas.util.acoustid;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import org.s16a.mcas.util.http.HTTPUtil;

public class AcoustID {

    private final static String PROPERTIES = "/acoustid.properties";

   /**
    * Chromaprint the file passed in
    */
   public static ChromaPrint chromaprint(File file, String fpcalc) throws IOException {
      final ProcessBuilder processBuilder = new ProcessBuilder(fpcalc, null);

      processBuilder.redirectErrorStream(true);
      processBuilder.command().set(1, file.getAbsolutePath());

      final Process fpcalcProcess = processBuilder.start();
      final BufferedReader br = new BufferedReader(new InputStreamReader(fpcalcProcess.getInputStream()));

      String line;
      String chromaprint = null;
      String duration = null;

      while ((line = br.readLine()) != null) {
         if (line.startsWith("FINGERPRINT=")) {
            chromaprint = line.substring("FINGERPRINT=".length());
         } else if (line.startsWith("DURATION=")) {
            duration = line.substring("DURATION=".length());
         }
      }

      return new ChromaPrint(chromaprint, duration);
   }

   /**
    * get the highest rated result
    */
   private static Result getBestResult(Results results) {
      if (results.results.size() > 0) {
         Result bestResult = results.results.get(0);

         double currentScore = Double.parseDouble(bestResult.score);

         for (final Result result : results.results) {
            final double score = Double.parseDouble(result.score);

            if (score > currentScore) {
               bestResult = result;
               currentScore = score;
            }
         }

         return bestResult;
      } else {
         return null;
      }
   }

   /**
    * get the Results object from JSON
    */
   private static Results getResults(String json) {
      final Gson gson = new Gson();

      return gson.fromJson(json, Results.class);
   }

   /**
    * do a ChromaPrint lookup and result a musicbrainz id
    */
   public static String lookup(ChromaPrint chromaprint) throws ClientProtocolException, IOException {
      final Properties properties = new Properties();

      properties.load(AcoustID.class.getResourceAsStream(PROPERTIES));

      final String url = properties.getProperty("url") + "?client=" + properties.getProperty("client") + "&meta=recordingids" + "&fingerprint=" + chromaprint.chromaprint + "&duration=" + chromaprint.duration;
      final HTTPUtil.Response response = HTTPUtil.get(url);
      final String json = response.response;
      final Results results = getResults(json);

      if (results.status.compareTo("ok") == 0) {
         final Result bestResult = getBestResult(results);

         if (bestResult != null) {
            if (bestResult.recordings.size() > 0) {
               return bestResult.recordings.get(0).id;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
