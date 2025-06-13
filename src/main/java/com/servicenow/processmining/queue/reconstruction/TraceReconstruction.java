package com.servicenow.processmining.queue.reconstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// Reconstructs queue identifiers and responsibilities for events in a trace.
public final class TraceReconstruction {

	public enum Responsibility {
		NOBODY,
		PROVIDER,
		REQUESTOR,
		TASK
	}

	public static final QueueIdentifier EMPTY_PROCESS_STEP_ID = new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, QueueIdentifier.EMPTY_ATTRIBUTE_VALUE);
	private static final QueueIdentifier UNDEFINED_QID = new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, "UNDEF_QUEUE_IDENTIFIER");

	private final Map<String, Responsibility> responsibilityMap;

	public TraceReconstruction(Map<String, Responsibility> responsibilityMap) {
		this.responsibilityMap = responsibilityMap;
	}

	public List<Event> reconstruct(List<Event> trace) {

		final List<Event> reconstructedEvents = new ArrayList<>();
		QueueIdentifier previouslyAssignedQueueIdentifier = UNDEFINED_QID;
		Responsibility previousResponsibility = Responsibility.NOBODY;
		for (Event event : trace) {
			final String processStepName = event.getProcessStep();
			final Responsibility responsibility = processStepName != null ?
					responsibilityMap.getOrDefault(processStepName, Responsibility.NOBODY) : previousResponsibility;
			final QueueIdentifier queueIdentifier = getQueueIdentifier(previousResponsibility, responsibility, previouslyAssignedQueueIdentifier, event);
			final Event newEvent = event.setResponsibility(responsibility);
			if (!queueIdentifier.equals(UNDEFINED_QID)) {
				reconstructedEvents.add(newEvent.setQueueIdentifier(queueIdentifier));
				previouslyAssignedQueueIdentifier = queueIdentifier;
			} else {
				reconstructedEvents.add(newEvent);
			}
			previousResponsibility = responsibility;
		}
		return reconstructedEvents;
	}

	private QueueIdentifier getQueueIdentifier(Responsibility previousResponsibility, Responsibility responsibility,
	                                           QueueIdentifier previousQueueIdentifier, Event event) {
		final QueueIdentifier snapshotQueueIdentifier = getSnapshotQueueIdentifier(event);
		final QueueIdentifier snapshotProcessStepIdentifier = event.getProcessStep() == null ? EMPTY_PROCESS_STEP_ID
				: new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, event.getProcessStep());
		final QueueIdentifier queueIdentifier = responsibility.equals(Responsibility.PROVIDER) ? snapshotQueueIdentifier : snapshotProcessStepIdentifier;
		return queueIdentifier.equals(previousQueueIdentifier) ? UNDEFINED_QID : queueIdentifier;
	}

	private static boolean isAttributeDefined(String value) {
		return !value.equals(QueueIdentifier.EMPTY_ATTRIBUTE_VALUE);
	}

	public QueueIdentifier getSnapshotQueueIdentifier(Event event) {
		final String teamId = event.getTeamOrEmpty();
		final String resourceId = event.getResourceOrEmpty();
		if (isAttributeDefined(teamId) && isAttributeDefined(resourceId)) {
			return new QueueIdentifier(QueueIdentifier.QueueType.RESOURCE, resourceId);
		} else if (isAttributeDefined(resourceId)) {
			return new QueueIdentifier(QueueIdentifier.QueueType.RESOURCE, resourceId);
		} else if (isAttributeDefined(teamId)) {
			return new QueueIdentifier(QueueIdentifier.QueueType.TEAM, teamId);
		}
		return QueueIdentifier.EMPTY;
	}
}
