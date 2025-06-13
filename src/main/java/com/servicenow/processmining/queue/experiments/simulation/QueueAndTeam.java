package com.servicenow.processmining.queue.experiments.simulation;

import java.util.Objects;

import com.servicenow.processmining.queue.reconstruction.QueueIdentifier;

/// Represents a queue and a team
public final class QueueAndTeam {

	private final QueueIdentifier fQueueIdentifier;
	private final String fTeam;

	public QueueAndTeam(QueueIdentifier queueIdentifier, String team) {
		fQueueIdentifier = queueIdentifier;
		fTeam = team;
	}

	public QueueIdentifier getQueueIdentifier() {
		return fQueueIdentifier;
	}

	public String getTeam() {
		return fTeam;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		QueueAndTeam that = (QueueAndTeam) o;
		return Objects.equals(fQueueIdentifier, that.fQueueIdentifier) && Objects.equals(fTeam, that.fTeam);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fQueueIdentifier, fTeam);
	}
}
