package com.servicenow.processmining.queue.examples.bpic.io;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XLog;

import com.servicenow.processmining.queue.experiments.io.XesLogReader;
import com.servicenow.processmining.queue.reconstruction.Event;

/// Converts the BPI Challenge incident log into the required internal representation
public final class BpiChallengeIncidentLogReader {

	public static Map<String, List<Event>> readLog(String logFile) throws Exception {
		final XLog xlog = XesLogReader.readXes(new File(logFile)).get(0);
		return XesLogReader.xesToTraces(xlog).stream()
				.map(pair -> {
					final String caseId = pair.getKey().entrySet().iterator().next().getValue().toString();
					final List<Event> events = pair.getValue().stream()
							.map(map -> {
								final long time = ((XAttributeTimestamp)map.get("time:timestamp")).getValue().getTime();
								final String processStep = map.get("lifecycle:transition").toString();
								final String team = map.get("org:group").toString();
								final String resource = map.get("org:resource").toString();
								return new Event(time, processStep, team, resource);

							}).collect(Collectors.toList());
					return Pair.of(caseId, events);
				}).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
	}
}
