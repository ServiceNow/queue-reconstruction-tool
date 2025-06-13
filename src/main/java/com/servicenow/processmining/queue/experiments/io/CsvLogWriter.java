package com.servicenow.processmining.queue.experiments.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriter;
import com.servicenow.processmining.queue.reconstruction.Event;

/// Writes a log to a CSV file.
public final class CsvLogWriter {

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private CsvLogWriter() {
	}

	public static void exportTransformedLog(Map<String, List<Event>> log, String filename) throws IOException {
		try (final BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(Files.newOutputStream(Paths.get(filename)), StandardCharsets.UTF_8))) {
			final CSVWriter writer = new CSVWriter(bufferedWriter);
			writer.writeNext(List.of("case_id", "timestamp", "activity", "team", "resource", "queue_type", "queue_id", "responsibility")
					.toArray(String[]::new));
			log.forEach((caseId, trace) ->
					trace.forEach(event -> {
						final Instant instant = Instant.ofEpochMilli(event.getTime());
						final LocalDateTime ldt = instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
						writer.writeNext(List.of(caseId, ldt.format(DATE_TIME_FORMATTER), event.getProcessStep(), event.getTeam(), event.getResource(),
								event.getQueueIdentifier() == null ? "" : event.getQueueIdentifier().getQueueType().toString(),
								event.getQueueIdentifier() == null ? "" : event.getQueueIdentifier().getId(),
								event.getResponsibility().toString()).toArray(String[]::new));
					}));
		}
	}
}
