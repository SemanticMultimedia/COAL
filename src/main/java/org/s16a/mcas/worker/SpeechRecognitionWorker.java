package org.s16a.mcas.worker;

import com.hp.hpl.jena.rdf.model.Property;
import org.s16a.mcas.MCAS;

public class SpeechRecognitionWorker extends PythonWorker implements Runnable {

	private static final Property TASK_QUEUE_NAME = MCAS.speech;

	public void run() {

		try {
			System.out.println("[x] Executing SpeechRecognition worker");
			executePythonWorker(
					TASK_QUEUE_NAME,
					"/usr/bin/python ./src/main/java/org/s16a/mcas/worker/SpeechRecognitionWorker.py ",
					"Extracting text was successful"
			);

		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

}