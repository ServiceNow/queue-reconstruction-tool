package com.servicenow.processmining.queue.examples.bpic.io;



import java.util.Objects;

/// Represents a performance spectrum segment
public final class Segment {
	private final String fActivity1;
	private final String fActivity2;

	public Segment(String activity1, String activity2) {
		fActivity1 = activity1;
		fActivity2 = activity2;
	}

	public String getActivity1() {
		return fActivity1;
	}

	public String getActivity2() {
		return fActivity2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Segment segment = (Segment) o;
		return Objects.equals(fActivity1, segment.fActivity1) && Objects.equals(fActivity2, segment.fActivity2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fActivity1, fActivity2);
	}

	@Override
	public String toString() {
		return "Segment{" +
				"fActivity1='" + fActivity1 + '\'' +
				", fActivity2='" + fActivity2 + '\'' +
				'}';
	}
}
