package com.servicenow.processmining.queue.examples.bpic.io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.servicenow.processmining.queue.reconstruction.Event;
import com.servicenow.processmining.queue.reconstruction.QueueIdentifier;

/// Exports the segments to be imported into the Performance Spectrum Miner
public final class PerformanceSpectrumSegmentExporter {

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
	private static final String CONFIG_FILE = "segment_dataset.segdir";
	private static final String CONFIG = "[GENERAL]\n" +
			"dateFormat = dd-MM-yyyy HH:mm:ss.SSS\n" +
			"zoneId = Europe/Amsterdam\n" +
			"startTime = 10-04-2012 00:00:00.000\n" +
			"endTime = 10-05-2012 00:00:00.000\n" +
			"name = SegmentClassifier\n" +
			"legend = Trivial%P0\n" +
			"classCount = 1";

	private final Map<String, List<Event>> fLog;
	private final String fPath;


	public PerformanceSpectrumSegmentExporter(Map<String, List<Event>> log, String path) {
		fLog = log;
		fPath = path;
	}

	public void export() throws FileNotFoundException {
		getSegments().entrySet().stream()
				.filter(x -> x.getValue().size() > 120)
				.forEach(entry -> {
					try {
						writeToDisk(fPath, entry.getKey(), entry.getValue());
					} catch (FileNotFoundException e) {
						throw new RuntimeException(e);
					}
				});
		try (PrintWriter pw = new PrintWriter(fPath + CONFIG_FILE)) {
			pw.print(CONFIG);
		}

		System.out.println("Performance spectrum files written to " + fPath);
		System.out.println("Open file " + CONFIG_FILE + " in the same directory to see the performance spectrum in the Performance Spectrum Miner");
	}

	private void writeToDisk(String path, Segment segment, List<SegmentOccurrence> occurrences) throws FileNotFoundException {
		final String filename =
				escapeSymbols(String.format("log.xes.%s!%s.seg", segment.getActivity1(), segment.getActivity2()));


		try (PrintWriter pw = new PrintWriter(fPath + filename)) {
			pw.println("caseID,startTime,ignored,durationMs,class");
			occurrences.forEach(o -> {
				final Instant instant = Instant.ofEpochMilli(o.getTime());
				final LocalDateTime ldt = instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
				pw.println(String.format("%s,%s,,%d,%d", o.getCaseId(), ldt.format(DATE_TIME_FORMATTER), o.getDuration(), o.getClazz()));
			});
		}


	}

	private static String escapeSymbols(String format) {
		return format.replace('/', '_')
				.replace('\\', '_')
				.replace('*', '_')
				.replace(':', '_');
	}

	private Map<Segment, List<SegmentOccurrence>> getSegments() {

		final long minTime = fLog.values().stream()
				.flatMap(Collection::stream)
				.map(Event::getTime)
				.min(Comparator.comparingLong(t -> t)).orElse(0L);
		final long maxTime = fLog.values().stream()
				.flatMap(Collection::stream)
				.map(Event::getTime)
				.max(Comparator.comparingLong(t -> t)).orElse(0L);
		final Instant instantMin = Instant.ofEpochMilli(minTime);
		final LocalDateTime ldtMin = instantMin.atOffset(ZoneOffset.UTC).toLocalDateTime();
		//System.out.println("Min time = " + ldtMin.format(DATE_TIME_FORMATTER));
		final Instant instantMax = Instant.ofEpochMilli(maxTime);
		final LocalDateTime ldtMax = instantMax.atOffset(ZoneOffset.UTC).toLocalDateTime();
		//System.out.println("Max time = " + ldtMax.format(DATE_TIME_FORMATTER));

		return fLog.entrySet().stream()
				.flatMap(x -> getSegmentsForTrace(x.getKey(), x.getValue()).stream())
				.collect(Collectors.groupingBy(SegmentOccurrence::getSegment, Collectors.toList()));
	}

	private List<SegmentOccurrence> getSegmentsForTrace(String caseId, List<Event> initialTrace) {
		final List<SegmentOccurrence> occurrences = new ArrayList<>();
		final List<Event> trace = initialTrace.stream().filter(x -> x.getQueueIdentifier() != null).collect(Collectors.toList());
		for (int i = 0; i < trace.size() - 1; i++) {
			final Event e1 = trace.get(i);
			final Event e2 = trace.get(i + 1);
			if (!e1.getQueueIdentifier().getQueueType().equals(QueueIdentifier.QueueType.RESOURCE)) {
				occurrences.add(new SegmentOccurrence(new Segment(e1.getQueueIdentifier().getQueueType().toString(), e1.getQueueIdentifier().getId()),
						caseId, e1.getTime(), e2.getTime() - e1.getTime(),
						2));
			} else {
				occurrences.add(new SegmentOccurrence(new Segment(e1.getQueueIdentifier().getQueueType().toString(), e1.getQueueIdentifier().getId()),
						caseId, e1.getTime(), e2.getTime() - e1.getTime(),
						2));
			}
		}
		return occurrences;
	}


}