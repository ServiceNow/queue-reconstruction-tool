package com.servicenow.processmining.queue.experiments.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.servicenow.processmining.queue.reconstruction.Event;
import com.servicenow.processmining.queue.reconstruction.QueueIdentifier;
import com.servicenow.processmining.queue.reconstruction.TraceReconstruction;

/// Generates an incident trace with queue identifiers in events
public final class TraceGenerator {

	private static final int REASSIGNMENT_PROBABILITY_PERCENT = 65;
	private final long fInitialTime;
	private final int fMaxDurationMs;
	private final int fEventNumber;
	private final int fTeamNumber;
	private final int fResourceInTeamNumber;
	private final Map<String, TraceReconstruction.Responsibility> fResponsibilityMap;
	private final List<String> fVariant;
	private final Random fRandom;
	private int fVariantIndex;
	private QueueIdentifier fPrevoiusQueueIdentifier = null;

	public TraceGenerator(long initialTime, int maxDurationMs, int eventNumber, int teamNumber, int resourceInTeamNumber,
	                      Map<String, TraceReconstruction.Responsibility> responsibilityMap, List<String> variant, Random random) {
		fInitialTime = initialTime;
		fMaxDurationMs = maxDurationMs;
		fEventNumber = eventNumber;
		fTeamNumber = teamNumber;
		fResourceInTeamNumber = resourceInTeamNumber;
		fResponsibilityMap = responsibilityMap;
		fVariant = variant;
		fRandom = random;
		fVariantIndex = 0;
	}

	public List<Event> generate() {
		final List<Event> trace = new ArrayList<>();
		for (int i = 0; i < fEventNumber && fVariantIndex < fVariant.size(); i++) {
			trace.add(trace.isEmpty() ? generateFirstEvent() : generateEvent(trace.get(i - 1)));
		}
		return trace;
	}

	private Event generateFirstEvent() {
		final QueueAndTeam queueAndTeam = generateQueueAndTeam();
		fPrevoiusQueueIdentifier = queueAndTeam.getQueueIdentifier();
		return new Event(fInitialTime, fVariant.get(fVariantIndex++), queueAndTeam.getTeam(),
				extractResource(queueAndTeam),
				fPrevoiusQueueIdentifier, fResponsibilityMap.get(fVariant.get(0)));
	}

	private String extractResource(QueueAndTeam queueAndTeam) {
		return queueAndTeam.getQueueIdentifier().getQueueType().equals(QueueIdentifier.QueueType.RESOURCE) ? queueAndTeam.getQueueIdentifier().getId() : QueueIdentifier.EMPTY_ATTRIBUTE_VALUE;
	}

	private Event generateEvent(Event previousEvent) {
		final long time = previousEvent.getTime() + getDuration();
		final String snapshotProcessStep = getProcessStepName();
		final String processStep = snapshotProcessStep.equals(QueueIdentifier.EMPTY_ATTRIBUTE_VALUE) ? previousEvent.getProcessStep() : snapshotProcessStep;
		final TraceReconstruction.Responsibility responsibility = processStep.equals(QueueIdentifier.EMPTY_ATTRIBUTE_VALUE) ? previousEvent.getResponsibility() : fResponsibilityMap.get(processStep);
		final QueueAndTeam snapshotQueueAndTeam = generateQueueAndTeam();
		final String team = snapshotQueueAndTeam.getTeam();
		final String resource = extractResource(snapshotQueueAndTeam);
		final QueueIdentifier queueIdentifier = responsibility.equals(TraceReconstruction.Responsibility.PROVIDER)
				? snapshotQueueAndTeam.getQueueIdentifier() : new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, processStep);
		QueueIdentifier qid = null;
		if (!queueIdentifier.equals(fPrevoiusQueueIdentifier)) {
			fPrevoiusQueueIdentifier = queueIdentifier;
			qid = queueIdentifier;
		}
		return new Event(time, processStep, team, resource, qid, responsibility);
	}

	private String getProcessStepName() {
		return fRandom.nextInt(100) < 100 - REASSIGNMENT_PROBABILITY_PERCENT ? fVariant.get(fVariantIndex++) : QueueIdentifier.EMPTY_ATTRIBUTE_VALUE;
	}

	private long getDuration() {
		return fRandom.nextInt(fMaxDurationMs);
	}

	private QueueAndTeam generateQueueAndTeam() {
		final int teamIndex = fRandom.nextInt(fTeamNumber);
		final int resourceIndex = fRandom.nextInt(fResourceInTeamNumber);
		return fRandom.nextBoolean() ?
				new QueueAndTeam(new QueueIdentifier(QueueIdentifier.QueueType.TEAM, IncidentProcess.getTeamName(teamIndex)), IncidentProcess.getTeamName(teamIndex))
				: new QueueAndTeam(new QueueIdentifier(QueueIdentifier.QueueType.RESOURCE, IncidentProcess.getResourceName(teamIndex, resourceIndex)), IncidentProcess.getTeamName(teamIndex));
	}
}
