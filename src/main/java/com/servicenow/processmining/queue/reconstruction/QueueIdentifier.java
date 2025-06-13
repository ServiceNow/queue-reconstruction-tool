package com.servicenow.processmining.queue.reconstruction;


import java.util.Objects;


/// Represents a queue identifier.
public final class QueueIdentifier {

	public static final String EMPTY_ATTRIBUTE_VALUE = "EMPTY";
	public static final QueueIdentifier EMPTY = new QueueIdentifier(QueueIdentifier.QueueType.EMPTY, EMPTY_ATTRIBUTE_VALUE);

	public enum QueueType {
		EMPTY,
		TEAM,
		RESOURCE,
		PROCESS_STEP
	}

	private final QueueType queueType;
	private final String id;

	public QueueIdentifier(QueueType queueType, String id) {
		this.queueType = queueType;
		this.id = id;
	}

	public QueueType getQueueType() {
		return queueType;
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		QueueIdentifier queueIdentifier = (QueueIdentifier) o;
		return queueType == queueIdentifier.queueType && Objects.equals(id, queueIdentifier.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(queueType, id);
	}

	@Override
	public String toString() {
		return "QueueIdentifier{" +
				"queueType=" + queueType +
				", id='" + id + '\'' +
				'}';
	}
}
