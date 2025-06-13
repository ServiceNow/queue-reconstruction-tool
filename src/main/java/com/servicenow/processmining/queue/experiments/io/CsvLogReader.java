package com.servicenow.processmining.queue.experiments.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.servicenow.processmining.queue.reconstruction.Event;

/// Reads a log from a CSV file.
public final  class CsvLogReader {

	private CsvLogReader() {
	}

	public static Map<String, List<Event>> readLog(String filename) throws CsvValidationException, IOException {

		final BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(Files.newInputStream(Paths.get(filename)), StandardCharsets.UTF_8));
		final CSVReader csvReader = new CSVReader(bufferedReader);
		csvReader.readNext();
		final Map<String, List<Event>> log = new HashMap<>();
		List<Event> trace = new ArrayList<>();
		String caseId = "";
		for (; ; ) {
			final String[] line = csvReader.readNext();
			if (line == null) {
				if (!trace.isEmpty()) {
					log.put(caseId, trace);
				}
				break;
			}
			final String newCaseId = line[0];
			if (!newCaseId.equals(caseId)) {
				if (!trace.isEmpty()) {
					log.put(caseId, trace);
				}
				trace = new ArrayList<>();
				caseId = newCaseId;
			}
			final Event event = new Event(LocalDateTime.parse(line[1], CsvLogWriter.DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
					line[2], line[3], line[4]);
			trace.add(event);
		}
		csvReader.close();
		return log;

	}

}
