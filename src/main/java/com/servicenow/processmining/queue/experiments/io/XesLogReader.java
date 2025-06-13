package com.servicenow.processmining.queue.experiments.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.tuple.Pair;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

/// Reads XES event logs
public final class XesLogReader {

	public static List<XLog> readXes(File logFile) throws Exception {
		final XFactory factory = XFactoryRegistry.instance().currentDefault();
		final String extension = logFile.getName().substring(logFile.getName().lastIndexOf('.'));
		InputStream fileInputStream;
		if (extension.equals(".gz")) {
			fileInputStream = new GZIPInputStream(new FileInputStream(logFile));
		} else if (extension.equals(".zip")) {
			fileInputStream = new ZipFile(logFile).getInputStream(new ZipFile(logFile).entries().nextElement());
		} else {
			fileInputStream = new BufferedInputStream(new FileInputStream(logFile));
		}
		XParser parser;
		if (logFile.getName().endsWith(".xes") || logFile.getName().endsWith(".xes.gz")) {
			parser = new XesXmlParser(factory);
		} else {
			parser = new XMxmlParser(factory);
		}
		return parser.parse(fileInputStream);
	}

	public static List<Pair<Map<String, Object>, List<Map<String, Object>>>> xesToTraces(XLog xLog) {
		return xLog.stream()
				.map(xTrace -> {
					final Map<String, Object> traceAttrs = new HashMap<>(xTrace.getAttributes());
					List<Map<String, Object>> events = xTrace.stream()
							.map(xEvent -> (Map<String, Object>) new HashMap<String, Object>(xEvent.getAttributes()))
							.collect(Collectors.toList());
					return Pair.of(traceAttrs, events);
				}).collect(Collectors.toList());
	}
}
