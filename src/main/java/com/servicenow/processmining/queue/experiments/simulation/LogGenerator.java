package com.servicenow.processmining.queue.experiments.simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.servicenow.processmining.queue.reconstruction.Event;

/// Generates a log of incidents
public final class LogGenerator {

	private final int fTraceNumber;
	private final int fMaxDurationMs;
	private final int fMaxEventNumber;
	private final int fTeamNumber;
	private final int fResourceInTeamNumber;
	private final List<List<String>> fVariant;
	private final Random fRandom;

	public LogGenerator(int traceNumber, int maxDurationMs, int maxEventNumber, int teamNumber, int resourceInTeamNumber, List<List<String>> variant, int seed) {
		fTraceNumber = traceNumber;
		fMaxDurationMs = maxDurationMs;
		fMaxEventNumber = maxEventNumber;
		fTeamNumber = teamNumber;
		fResourceInTeamNumber = resourceInTeamNumber;
		fVariant = variant;
		fRandom = new Random(seed);
	}

	public Map<String, List<Event>> generate(){
		long time = 1733405216000L;
		final Map<String, List<Event>> log = new HashMap<>();
		for(int i = 0; i < fTraceNumber; i++) {
			final TraceGenerator traceGenerator = new TraceGenerator(time, fMaxDurationMs, fMaxEventNumber, fTeamNumber, fResourceInTeamNumber, IncidentProcess.RESPONSIBILITY_MAP, fVariant.get(fRandom.nextInt(fVariant.size())), fRandom);
			log.put(Integer.toString(i), traceGenerator.generate());
			time += (long)(fMaxDurationMs*100.0/(fRandom.nextInt(100)+1));
		}
		return log;
	}
}
