package com.servicenow.processmining.queue.experiments.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import com.servicenow.processmining.queue.reconstruction.TraceReconstruction;

/// 
public final class JsonUtils {

	private JsonUtils() {
	}

	public static Map<String, TraceReconstruction.Responsibility> readJson(String filename) throws IOException {
		final String jsonInput = Files.readString(Paths.get(filename), StandardCharsets.UTF_8);
		return new ObjectMapper().readValue(jsonInput, new TypeReference<>() {
		});
	}

	public static void writeJson(String filename, Map<String, TraceReconstruction.Responsibility> map) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8), map);

	}
}
