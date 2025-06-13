package com.servicenow.processmining.queue.experiments.reconstruction.synthetic;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.servicenow.processmining.queue.experiments.io.CsvLogWriter;
import com.servicenow.processmining.queue.reconstruction.Event;
import com.servicenow.processmining.queue.reconstruction.TraceReconstruction;
import com.servicenow.processmining.queue.experiments.simulation.IncidentProcess;
import com.servicenow.processmining.queue.experiments.simulation.LogGenerator;

/// Experiment class for the synthetic log generation, queue reconstruction, and queue identifier comparison against the ground truth
public class Experiment {

	private final String fPath;
	private final LogGenerator fLogGenerator;
	private boolean fExportLogs;


	public Experiment(int traceNumber, int maxDurationMs, int maxEventNumber, int teamNumber, int resourceInTeamNumber, int seed, String path, boolean exportLogs) {

		if (traceNumber <= 0) {
			throw new IllegalArgumentException("The number of traces must be greater than 0");
		}
		if (maxDurationMs <= 0) {
			throw new IllegalArgumentException("The maximum duration must be greater than 0");
		}

		if (maxEventNumber <= 0) {
			throw new IllegalArgumentException("The maximum number of events must be greater than 0");
		}

		if (teamNumber <= 0) {
			throw new IllegalArgumentException("The number of teams must be greater than 0");
		}

		if (resourceInTeamNumber <= 0) {
			throw new IllegalArgumentException("The number of resources in a team must be greater than 0");
		}

		if (path.isBlank()) {
			throw new IllegalArgumentException("The path must not be empty or blank");
		}
		fPath = path + "/";
		fLogGenerator = new LogGenerator(traceNumber, maxDurationMs, maxEventNumber, teamNumber, resourceInTeamNumber, IncidentProcess.VARIANTS, seed);
		fExportLogs = exportLogs;

	}

	public int run() throws IOException {
		new File(fPath).mkdirs();
		final Map<String, List<Event>> log = fLogGenerator.generate();
		if (fExportLogs) {
			CsvLogWriter.exportTransformedLog(log, fPath + "initial_log.csv");
			System.out.println("Initial log has been exported to " + fPath + "initial_log.csv");
		}

		final Map<String, List<Event>> reconstructedLog = log.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						x -> {
							final TraceReconstruction traceReconstruction = new TraceReconstruction(IncidentProcess.RESPONSIBILITY_MAP);
							return traceReconstruction.reconstruct(removeQueueInformation(x.getValue()));
						}));

		if (fExportLogs) {
			CsvLogWriter.exportTransformedLog(reconstructedLog, fPath + "reconstructed_log.csv");
			System.out.println("Reconstructed log has been exported to " + fPath + "reconstructed_log.csv");
		}
		final int errorNumber = log.entrySet().stream()
				.mapToInt(x -> {
					List<Event> reconstructedTrace = reconstructedLog.get(x.getKey());
					int errorCount = 0;
					for (int i = 0; i < x.getValue().size(); i++) {
						final Event expected = x.getValue().get(i);
						final Event real = reconstructedTrace.get(i);
						if (!real.equals(expected)) {
							errorCount++;
						}
					}
					return errorCount;
				}).sum();
		return errorNumber;
	}

	private List<Event> removeQueueInformation(List<Event> events) {
		return events.stream()
				.map(e -> new Event(e.getTime(), e.getProcessStep(), e.getTeam(), e.getResource()))
				.collect(Collectors.toList());
	}


}
