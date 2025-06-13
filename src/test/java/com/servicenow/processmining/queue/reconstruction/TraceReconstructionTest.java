package com.servicenow.processmining.queue.reconstruction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.servicenow.processmining.queue.experiments.simulation.IncidentProcess;

public final class TraceReconstructionTest {

	private static final String TEAM_1 = IncidentProcess.getTeamName(1);
	private static final String TEAM_2 = IncidentProcess.getTeamName(2);
	private static final String TEAM_1_RES_1 = IncidentProcess.getResourceName(1, 1);
	private static final String TEAM_1_RES_2 = IncidentProcess.getResourceName(1, 2);
	private static final String TEAM_2_RES_3 = IncidentProcess.getResourceName(2, 3);
	private static final String TEAM_2_RES_4 = IncidentProcess.getResourceName(2, 4);

	@Test
	public void testEmptyTrace() {
		final TraceReconstruction traceReconstruction = new TraceReconstruction(IncidentProcess.RESPONSIBILITY_MAP);
		assertTrue(traceReconstruction.reconstruct(List.of()).isEmpty());
	}

	@Test
	public void testSingleEventTrace() {
		final TraceReconstruction traceReconstruction = new TraceReconstruction(IncidentProcess.RESPONSIBILITY_MAP);
		final List<Event> trace = List.of(new Event(0, IncidentProcess.ProcessStepNames.NEW.toString(), QueueIdentifier.EMPTY_ATTRIBUTE_VALUE, QueueIdentifier.EMPTY_ATTRIBUTE_VALUE));
		assertEquals(1, traceReconstruction.reconstruct(trace).size());
		final Event event = traceReconstruction.reconstruct(trace).get(0);
		assertEquals(QueueIdentifier.EMPTY, event.getQueueIdentifier());
	}

	@Test
	public void testConcurrentAssignment() {
		final TraceReconstruction traceReconstruction = new TraceReconstruction(IncidentProcess.RESPONSIBILITY_MAP);
		final List<Event> initialTrace = List.of(
				new Event(0, IncidentProcess.ProcessStepNames.NEW.toString(), TEAM_1, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.IN_PROGRESS.toString(), TEAM_1, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.RESOLVED.toString(), TEAM_1, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.CLOSED.toString(), TEAM_1, TEAM_1_RES_1));
		assertEquals(4, traceReconstruction.reconstruct(initialTrace).size());
		final List<Event> trace = traceReconstruction.reconstruct(initialTrace);
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.RESOURCE, TEAM_1_RES_1), trace.get(0).getQueueIdentifier());
		assertNull(trace.get(1).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.RESOLVED.toString()), trace.get(2).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.CLOSED.toString()), trace.get(3).getQueueIdentifier());

	}

	@Test
	public void testTeamAssignment() {
		final TraceReconstruction traceReconstruction = new TraceReconstruction(IncidentProcess.RESPONSIBILITY_MAP);
		final List<Event> initialTrace = List.of(
				new Event(0, IncidentProcess.ProcessStepNames.NEW.toString(), TEAM_1, QueueIdentifier.EMPTY_ATTRIBUTE_VALUE),
				new Event(0, IncidentProcess.ProcessStepNames.IN_PROGRESS.toString(), TEAM_1, QueueIdentifier.EMPTY_ATTRIBUTE_VALUE),
				new Event(0, IncidentProcess.ProcessStepNames.RESOLVED.toString(), TEAM_1, QueueIdentifier.EMPTY_ATTRIBUTE_VALUE),
				new Event(0, IncidentProcess.ProcessStepNames.CLOSED.toString(), TEAM_1, QueueIdentifier.EMPTY_ATTRIBUTE_VALUE));
		assertEquals(4, traceReconstruction.reconstruct(initialTrace).size());
		final List<Event> trace = traceReconstruction.reconstruct(initialTrace);
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.TEAM, TEAM_1), trace.get(0).getQueueIdentifier());
		assertNull(trace.get(1).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.RESOLVED.toString()), trace.get(2).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.CLOSED.toString()), trace.get(3).getQueueIdentifier());

	}

	@Test
	public void testTeamResourceAssignment() {
		final TraceReconstruction traceReconstruction = new TraceReconstruction(IncidentProcess.RESPONSIBILITY_MAP);
		final List<Event> initialTrace = List.of(
				new Event(0, IncidentProcess.ProcessStepNames.NEW.toString(), TEAM_1, QueueIdentifier.EMPTY_ATTRIBUTE_VALUE),
				new Event(0, IncidentProcess.ProcessStepNames.IN_PROGRESS.toString(), TEAM_1, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.RESOLVED.toString(), TEAM_1, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.CLOSED.toString(), TEAM_1, TEAM_1_RES_1));
		assertEquals(4, traceReconstruction.reconstruct(initialTrace).size());
		final List<Event> trace = traceReconstruction.reconstruct(initialTrace);
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.TEAM, TEAM_1), trace.get(0).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.RESOURCE, TEAM_1_RES_1), trace.get(1).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.RESOLVED.toString()), trace.get(2).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.CLOSED.toString()), trace.get(3).getQueueIdentifier());

	}

	@Test
	public void testResourceAssignment() {
		final TraceReconstruction traceReconstruction = new TraceReconstruction(IncidentProcess.RESPONSIBILITY_MAP);
		final List<Event> initialTrace = List.of(
				new Event(0, IncidentProcess.ProcessStepNames.NEW.toString(), QueueIdentifier.EMPTY_ATTRIBUTE_VALUE, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.IN_PROGRESS.toString(), QueueIdentifier.EMPTY_ATTRIBUTE_VALUE, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.RESOLVED.toString(), QueueIdentifier.EMPTY_ATTRIBUTE_VALUE, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.CLOSED.toString(), QueueIdentifier.EMPTY_ATTRIBUTE_VALUE, TEAM_1_RES_1));
		assertEquals(4, traceReconstruction.reconstruct(initialTrace).size());
		final List<Event> trace = traceReconstruction.reconstruct(initialTrace);
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.RESOURCE, TEAM_1_RES_1), trace.get(0).getQueueIdentifier());
		assertNull(trace.get(1).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.RESOLVED.toString()), trace.get(2).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.CLOSED.toString()), trace.get(3).getQueueIdentifier());

	}

	@Test
	public void testConcurrentReassignment() {
		final TraceReconstruction traceReconstruction = new TraceReconstruction(IncidentProcess.RESPONSIBILITY_MAP);
		final List<Event> initialTrace = List.of(
				new Event(0, IncidentProcess.ProcessStepNames.NEW.toString(), TEAM_1, TEAM_1_RES_1),
				new Event(0, IncidentProcess.ProcessStepNames.IN_PROGRESS.toString(), TEAM_1, TEAM_1_RES_2),
				new Event(0, IncidentProcess.ProcessStepNames.RESOLVED.toString(), TEAM_2, TEAM_2_RES_3),
				new Event(0, IncidentProcess.ProcessStepNames.CLOSED.toString(), TEAM_2, TEAM_2_RES_4));
		assertEquals(4, traceReconstruction.reconstruct(initialTrace).size());
		final List<Event> trace = traceReconstruction.reconstruct(initialTrace);
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.RESOURCE, TEAM_1_RES_1), trace.get(0).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.RESOURCE, TEAM_1_RES_2), trace.get(1).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.RESOLVED.toString()), trace.get(2).getQueueIdentifier());
		assertEquals(new QueueIdentifier(QueueIdentifier.QueueType.PROCESS_STEP, IncidentProcess.ProcessStepNames.CLOSED.toString()), trace.get(3).getQueueIdentifier());

	}


}