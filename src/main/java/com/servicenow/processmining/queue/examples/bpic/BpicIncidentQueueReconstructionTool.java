package com.servicenow.processmining.queue.examples.bpic;

///  CLI tool for BPIC incident queue reconstruction
public final class BpicIncidentQueueReconstructionTool {
	public static void main(String[] args) {

		try {
			System.out.println("BPIC Incident queue reconstruction tool v.1.0");
			if(args.length != 2) {
				System.err.println("Arguments <BPIC_2013_Incident_xes_log> <output_path>");
				return;
			}
			new BpicIncidentExample(args[0], args[1]).run();

		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
