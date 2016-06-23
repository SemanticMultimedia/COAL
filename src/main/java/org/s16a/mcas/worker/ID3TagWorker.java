package org.s16a.mcas.worker;

import org.s16a.mcas.MCAS;

public class ID3TagWorker extends PythonWorker implements Runnable {

    private static final String TASK_QUEUE_NAME = MCAS.id3tags.toString();

    public void run() {

        try {

            executePythonWorker(
                    TASK_QUEUE_NAME,
                    "/usr/bin/python ./src/main/java/org/s16a/mcas/worker/ID3TagWorker.py ",
                    "Extracting ID3-Tags was successful"
            );

        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

}
