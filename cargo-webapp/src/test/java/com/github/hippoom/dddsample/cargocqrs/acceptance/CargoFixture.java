package com.github.hippoom.dddsample.cargocqrs.acceptance;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;
import com.github.hippoom.dddsample.cargocqrs.rest.RegisterCargoRequest;
import com.github.hippoom.dddsample.cargocqrs.rest.RegisterHandlingEventRequest;
import com.github.hippoom.dddsample.cargocqrs.rest.RouteCandidateDto;

public class CargoFixture {

	private WebApplicationContext wac;

	public CargoFixture(WebApplicationContext wac) {
		this.wac = wac;
	}

	public String aNewCargoIsRegisteredWith(String origin, String destination,
			Date arrivalDeadline) throws Exception {
		return mockMvc()
				.perform(
						put("/cargo").content(
								json(new RegisterCargoRequest(origin,
										destination, arrivalDeadline)))
								.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsString();

	}

	public ResultActions requestPossibleRoutesFor(String trackingId)
			throws Exception {
		return mockMvc().perform(get("/routes/" + trackingId)).andDo(print());
	}

	public ResultActions findCargoBy(String trackingId) throws Exception {
		return mockMvc().perform(get("/cargo/" + trackingId)).andDo(print())
				.andExpect(status().isOk());

	}

	public ResultActions assignCargoToRoute(String trackingId, LegDto... legs)
			throws Exception, JsonProcessingException {
		return mockMvc().perform(
				post("/cargo/" + trackingId).content(
						json(new RouteCandidateDto(legs))).contentType(
						MediaType.APPLICATION_JSON)).andDo(print());
	}

	public ResultActions aHandlingEventRegistered(String trackingId,
			HandlingType type, String location, String voyageNumber,
			Date completionTime) throws Exception, JsonProcessingException {

		return mockMvc()
				.perform(
						put("/handlingevent/")
								.content(
										new ObjectMapper()
												.writeValueAsBytes(new RegisterHandlingEventRequest(
														trackingId, type
																.getCode(),
														location, voyageNumber,
														completionTime)))
								.contentType(MediaType.APPLICATION_JSON))
				.andDo(print());
	}

	private MockMvc mockMvc() {
		return webAppContextSetup(this.wac).build();
	}

	private String json(Object object) throws JsonProcessingException,
			UnsupportedEncodingException {
		return new String(new ObjectMapper().writeValueAsBytes(object), "UTF-8");
	}
}
