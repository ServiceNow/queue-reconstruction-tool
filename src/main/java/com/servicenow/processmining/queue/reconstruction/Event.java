package com.servicenow.processmining.queue.reconstruction;

import java.util.Objects;

/// Represents an event in a log.
public final class Event {

	private final long time;
	private final String processStep;
	private final String team;
	private final String resource;
	private final QueueIdentifier queueIdentifier;
	private final TraceReconstruction.Responsibility responsibility;

	public Event(long time, String processStep, String team, String resource, QueueIdentifier queueIdentifier, TraceReconstruction.Responsibility responsibility) {
		this.time = time;
		this.processStep = processStep;
		this.team = team;
		this.resource = resource;
		this.queueIdentifier = queueIdentifier;
		this.responsibility = responsibility;
	}

	public Event(long time, String processStep, String team, String resource) {
		this(time, processStep, team, resource, null, null);
	}

	public long getTime() {
		return time;
	}

	public String getProcessStep() {
		return processStep;
	}

	public String getTeam() {
		return team;
	}

	public String getResource() {
		return resource;
	}

	public QueueIdentifier getQueueIdentifier() {
		return queueIdentifier;
	}

	public TraceReconstruction.Responsibility getResponsibility() {
		return responsibility;
	}

	public Event setResponsibility(TraceReconstruction.Responsibility currentResponsibility) {
		return new Event(time, processStep, team, resource, queueIdentifier, currentResponsibility);

	}

	public Event setQueueIdentifier(QueueIdentifier qidToAssign) {
		return new Event(time, processStep, team, resource, qidToAssign, responsibility);
	}

	public String getTeamOrEmpty() {
		return team == null ? QueueIdentifier.EMPTY_ATTRIBUTE_VALUE : team;
	}

	public String getResourceOrEmpty() {
		return resource == null ? QueueIdentifier.EMPTY_ATTRIBUTE_VALUE : resource;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Event event = (Event) o;
		return time == event.time && Objects.equals(processStep, event.processStep) && Objects.equals(team, event.team) && Objects.equals(resource, event.resource) && Objects.equals(queueIdentifier, event.queueIdentifier) && responsibility == event.responsibility;
	}

	@Override
	public int hashCode() {
		return Objects.hash(time, processStep, team, resource, queueIdentifier, responsibility);
	}

	@Override
	public String toString() {
		return "Event{" +
				"time=" + time +
				", processStep='" + processStep + '\'' +
				", team='" + team + '\'' +
				", resource='" + resource + '\'' +
				", queueIdentifier=" + queueIdentifier +
				", responsibility=" + responsibility +
				'}';
	}
}

