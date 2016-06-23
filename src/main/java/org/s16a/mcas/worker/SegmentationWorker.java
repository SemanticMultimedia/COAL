package org.s16a.mcas.worker;

import org.s16a.mcas.MCAS;

public class SegmentationWorker extends PythonWorker implements Runnable {

	private static final String TASK_QUEUE_NAME = MCAS.segments.toString();

	public void run() {

        try {

            executePythonWorker(
                    TASK_QUEUE_NAME,
                    "/usr/bin/python ./src/main/java/org/s16a/mcas/worker/SegmentationWorker.py ",
                    "Extracting segments was successful"
            );

        } catch (Exception e) {
            System.out.println(e.toString());
        }

	}

}

