package com.servicenow.processmining.queue.experiments.reconstruction.log;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.servicenow.processmining.queue.experiments.io.CsvLogReader;
import com.servicenow.processmining.queue.experiments.io.CsvLogWriter;
import com.servicenow.processmining.queue.experiments.io.JsonUtils;
import com.servicenow.processmining.queue.reconstruction.Event;
import com.servicenow.processmining.queue.reconstruction.TraceReconstruction;

///
public class QueueReconstructionTool {

	public static void main(String[] args) {

		try {
			System.out.println("Queue reconstruction tool v.1.0");
			if (args.length != 3) {
				System.err.println("Use arguments: <input_log_filename> <responsibility_json_file> <output_log_filename>");
				return;
			}
			Map<String, TraceReconstruction.Responsibility> responsibilityMapTmp = null;
			try {
				responsibilityMapTmp = JsonUtils.readJson(args[1]);
			} catch (Exception e) {
				System.err.println("Cannot read responsibility map in file " + args[1] + " :");
			}
			final Map<String, TraceReconstruction.Responsibility> responsibilityMap = responsibilityMapTmp;
			final Map<String, List<Event>> reconstructedLog = CsvLogReader.readLog(args[0]).entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey,
							x -> new TraceReconstruction(responsibilityMap)
									.reconstruct(x.getValue())));
			CsvLogWriter.exportTransformedLog(reconstructedLog, args[2]);
			System.out.println("Log has been exported to " + args[2]);

		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
