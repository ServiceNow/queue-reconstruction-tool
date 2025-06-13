package com.servicenow.processmining.queue.examples.bpic;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.servicenow.processmining.queue.examples.bpic.io.BpiChallengeIncidentLogReader;
import com.servicenow.processmining.queue.experiments.io.CsvLogWriter;
import com.servicenow.processmining.queue.examples.bpic.io.PerformanceSpectrumSegmentExporter;
import com.servicenow.processmining.queue.reconstruction.Event;
import com.servicenow.processmining.queue.reconstruction.TraceReconstruction;

// Describes the BPI Challenge 2013 Incident process properties and reconstructs queues
public final class BpicIncidentExample {

	public enum ProcessStepNames {
		EMPTY(""),
		AWAITING_ASSIGNMENT("Awaiting Assignment"),
		IN_PROGRESS("In Progress"),
		IN_CALL("In Call"),
		WAIT_USER("Wait - User"),
		WAIT_CUSTOMER("Wait - Customer"),
		WAIT("Wait"),
		WAIT_IMPLEMENTATION("Wait - Implementation"),
		WAIT_VENDOR("Wait - Vendor"),
		ASSIGNED("Assigned"),
		UNMATCHED("Unmatched"),
		RESOLVED("Resolved"),
		CLOSED("Closed"),
		CANCELLED("Cancelled");

		private final String value;

		ProcessStepNames(final String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static final Map<String, TraceReconstruction.Responsibility> RESPONSIBILITY_MAP = Stream.concat(Map.of(
							ProcessStepNames.AWAITING_ASSIGNMENT.toString(), TraceReconstruction.Responsibility.PROVIDER,
							ProcessStepNames.IN_PROGRESS.toString(), TraceReconstruction.Responsibility.PROVIDER,
							ProcessStepNames.EMPTY.toString(), TraceReconstruction.Responsibility.PROVIDER,
							ProcessStepNames.IN_CALL.toString(), TraceReconstruction.Responsibility.PROVIDER,
							ProcessStepNames.ASSIGNED.toString(), TraceReconstruction.Responsibility.PROVIDER,
							ProcessStepNames.WAIT_CUSTOMER.toString(), TraceReconstruction.Responsibility.REQUESTOR,
							ProcessStepNames.WAIT_USER.toString(), TraceReconstruction.Responsibility.TASK,
							ProcessStepNames.WAIT_IMPLEMENTATION.toString(), TraceReconstruction.Responsibility.TASK,
							ProcessStepNames.WAIT_VENDOR.toString(), TraceReconstruction.Responsibility.TASK,
							ProcessStepNames.UNMATCHED.toString(), TraceReconstruction.Responsibility.NOBODY).entrySet().stream(),
					Map.of(ProcessStepNames.RESOLVED.toString(), TraceReconstruction.Responsibility.NOBODY,
							ProcessStepNames.CLOSED.toString(), TraceReconstruction.Responsibility.NOBODY,
							ProcessStepNames.CANCELLED.toString(), TraceReconstruction.Responsibility.NOBODY).entrySet().stream())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	private final String fBpicLogPath;
	private final String fOutputDirPath;
	private final String fPerformanceSpectrumOutputDirPath;

	public BpicIncidentExample(String bpicLogPath, String outputDirPath) {
		if (bpicLogPath.isBlank()) {
			throw new IllegalArgumentException("The BPIC log path must not be empty or blank");
		}

		if (outputDirPath.isBlank()) {
			throw new IllegalArgumentException("The output path must not be empty or blank");
		}

		fBpicLogPath = bpicLogPath + "/";
		fOutputDirPath = outputDirPath + "/";
		fPerformanceSpectrumOutputDirPath = outputDirPath + "/performance_spectrum/";
	}

	public void run() throws Exception {
		new File(fPerformanceSpectrumOutputDirPath).mkdirs();
		final Map<String, List<Event>> log = BpiChallengeIncidentLogReader.readLog(fBpicLogPath);
		final Map<String, List<Event>> reconstructedLog = log.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						x -> new TraceReconstruction(RESPONSIBILITY_MAP).reconstruct(x.getValue())));
		CsvLogWriter.exportTransformedLog(reconstructedLog, fOutputDirPath + "bpic_reconstructed_log.csv");
		System.out.println("Reconstructed log has been exported to " + fOutputDirPath + "bpic_reconstructed_log.csv");
		new PerformanceSpectrumSegmentExporter(reconstructedLog, fPerformanceSpectrumOutputDirPath).export();
	}

}
