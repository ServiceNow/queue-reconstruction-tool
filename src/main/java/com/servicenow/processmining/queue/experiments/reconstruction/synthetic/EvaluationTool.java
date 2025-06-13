package com.servicenow.processmining.queue.experiments.reconstruction.synthetic;

/// Evaluates the queue reconstruction approach
public class EvaluationTool {

	public static void main(String[] args) {

		try {
			System.out.println("Queue reconstruction evaluation tool v.1.0");
			if(args.length != 7) {
				System.err.println("Arguments: <path> <traceNumber> <maxDurationMs> <maxEventNumber> <teamNumber> <resourceInTeamNumber> <seed>");
				return;
			}
			final String path = args[0];
			int traceNumber = Integer.parseInt(args[1]);
			int maxDurationMs = Integer.parseInt(args[2]);
			int maxEventNumber = Integer.parseInt(args[3]);
			int teamNumber = Integer.parseInt(args[4]);
			int resourceInTeamNumber = Integer.parseInt(args[5]);
			int seed = Integer.parseInt(args[6]);
			final int errorCount = new Experiment(traceNumber, maxDurationMs, maxEventNumber, teamNumber, resourceInTeamNumber, seed, path, true).run();
			System.out.println("Error count: " + errorCount);

		} catch (Exception e) {
			System.err.println(e);
		}
	}


}
