package com.github.hippoom.dddsample.cargocqrs.pathfinder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.api.TransitPath;

public class TransitPathTranslator {
	public List<TransitPath> from(String json) throws JsonParseException,
			JsonMappingException, IOException {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		return objectMapper.readValue(json,
				new TypeReference<List<TransitPath>>() {
				});
	}
}
