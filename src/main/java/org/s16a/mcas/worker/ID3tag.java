package org.s16a.mcas.worker;

import org.s16a.mcas.Enqueuer;
import org.s16a.mcas.MCAS;

import java.io.*;
/**
 * Created by davidkreidler on 02.06.16.
 */
public class ID3tag {

    public static void main(String[] argv) throws Exception {
        String s = null;
        String file = getFilePath(String fileName);
        try {
            Process p = Runtime.getRuntime().exec("python mutagen-inspect.py" + file);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            System.out.println("Output: \n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            System.out.println("error: \n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            Resource r = model.createResource();
		    /*r.addLiteral(DC.format, format);*/
            r.addLiteral(model.createProperty("ID3"), s);

            model.getResource(url).addProperty(MCAS.mediainfo, r);
            System.out.println(model.getResource(url).addProperty(MCAS.mediainfo, r));
            FileWriter out = new FileWriter(modelFileName);

            try {
                System.out.println("ID3tag try save");
                model.write(out, "TURTLE");
            } finally {
                try {
                    out.close();

                    try {
                        Enqueuer.workerFinished(MCAS.mediainfo, cache);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException closeException) {
                    // ignore
                    System.out.println("Fehler!!!");
                }
            }

            System.exit(0);
        } catch (IOException e) {
            System.out.println("exception happened");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
