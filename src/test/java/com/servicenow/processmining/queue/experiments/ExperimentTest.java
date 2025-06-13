package com.servicenow.processmining.queue.experiments;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.Test;

import com.servicenow.processmining.queue.experiments.reconstruction.synthetic.Experiment;

public class ExperimentTest {

	@Test
	public void testExperiment() {
		 Experiment experiment = new Experiment(10000, 1000000, 10, 10, 10, 0, ".", false);
		try {
			assertEquals(0, experiment.run());
		} catch (IOException e) {
			fail(e);
		}
	}

}