package com.servicenow.processmining.queue.experiments.simulation;

import java.util.List;
import java.util.Map;

import com.servicenow.processmining.queue.reconstruction.QueueIdentifier;
import com.servicenow.processmining.queue.reconstruction.TraceReconstruction;

/// Defines properties of the simulated Incident process
public final class IncidentProcess {

	public enum ProcessStepNames {
		EMPTY(QueueIdentifier.EMPTY_ATTRIBUTE_VALUE),
		NEW("New"),
		IN_PROGRESS("InProgress"),
		AWAITING_CALLER("AwaitingCaller"),
		AWAITING_PROBLEM("AwaitingProblem"),
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

	public static final Map<String, TraceReconstruction.Responsibility> RESPONSIBILITY_MAP = Map.of(
			ProcessStepNames.NEW.toString(), TraceReconstruction.Responsibility.PROVIDER,
			ProcessStepNames.IN_PROGRESS.toString(), TraceReconstruction.Responsibility.PROVIDER,
			ProcessStepNames.EMPTY.toString(), TraceReconstruction.Responsibility.PROVIDER,
			ProcessStepNames.AWAITING_PROBLEM.toString(), TraceReconstruction.Responsibility.TASK,
			ProcessStepNames.AWAITING_CALLER.toString(), TraceReconstruction.Responsibility.REQUESTOR,
			ProcessStepNames.RESOLVED.toString(), TraceReconstruction.Responsibility.NOBODY,
			ProcessStepNames.CLOSED.toString(), TraceReconstruction.Responsibility.NOBODY,
			ProcessStepNames.CANCELLED.toString(), TraceReconstruction.Responsibility.NOBODY);

	public static final List<List<String>> VARIANTS = List.of(
			List.of(ProcessStepNames.NEW.toString(),
					ProcessStepNames.RESOLVED.toString(),
					ProcessStepNames.CLOSED.toString()),
			List.of(ProcessStepNames.NEW.toString(),
					ProcessStepNames.IN_PROGRESS.toString(),
					ProcessStepNames.AWAITING_PROBLEM.toString(),
					ProcessStepNames.IN_PROGRESS.toString(),
					ProcessStepNames.CANCELLED.toString()),
			List.of(ProcessStepNames.NEW.toString(),
					ProcessStepNames.IN_PROGRESS.toString(),
					ProcessStepNames.AWAITING_CALLER.toString(),
					ProcessStepNames.IN_PROGRESS.toString(),
					ProcessStepNames.RESOLVED.toString(),
					ProcessStepNames.CLOSED.toString()));

	public static String getTeamName(int id) {
		return "team_" + id;
	}

	public static String getResourceName(int teamId, int resourceId) {
		return "res_" + teamId + "_" + resourceId;
	}


}
