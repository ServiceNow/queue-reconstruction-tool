package com.servicenow.processmining.queue.examples.bpic.io;

/// Represents a performance spectrum segment occurrence
public final class SegmentOccurrence {

	private final Segment fSegment;
	private final String fCaseId;
	private final long fTime;
	private final long fDuration;
	private final int fClazz;

	public SegmentOccurrence(Segment segment, String caseId, long time, long duration, int clazz) {
		fSegment = segment;
		fCaseId = caseId;
		fTime = time;
		fDuration = duration;
		fClazz = clazz;
	}

	public Segment getSegment() {
		return fSegment;
	}

	public String getCaseId() {
		return fCaseId;
	}

	public long getTime() {
		return fTime;
	}

	public long getDuration() {
		return fDuration;
	}

	public int getClazz() {
		return fClazz;
	}
}