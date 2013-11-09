package com.github.hippoom.dddsample.cargocqrs.acceptance;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.RECEIVE;
import static com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus.NOT_ROUTED;
import static com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus.ROUTED;
import static com.github.hippoom.dddsample.cargocqrs.core.TransportStatus.NOT_RECEIVED;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.github.hippoom.dddsample.cargocqrs.config.Configurations;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus;
import com.github.hippoom.dddsample.cargocqrs.core.TransportStatus;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;
import com.github.hippoom.dddsample.cargocqrs.rest.RegisterCargoRequest;
import com.github.hippoom.dddsample.cargocqrs.rest.RouteCandidateDto;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:acceptance.xml")
public class CargoAdminSteps {

	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private Configurations configurations;

	private MocoHttpServer moco;

	private String trackingId;

	private ResultActions cargo;

	private String origin = "CNSHA";

	private String destination = "CNPEK";

	private LegDto leg = new LegDto(0, "CM001", origin, destination,
			tommorrow(), twoDaysLater());

	private Date arrivalDeadline = aWeekLater();

	private ResultActions routeCandidates;

	@When("^I fill the form with origin, destination and arrival deadline$")
	public void I_fill_the_form_with_origin_destination_and_arrival_deadline()
			throws Throwable {
		this.trackingId = aNewCargoIsRegistered();
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^a new cargo is registered$")
	public void a_new_cargo_is_registered() throws Throwable {
		assertThat(trackingId, not(nullValue()));
	}

	@Then("^the cargo is not routed$")
	public void the_cargo_is_not_routed() throws Throwable {
		theRoutingStatusShouldBe(NOT_ROUTED);
	}

	@Then("^the transport status of the cargo is NOT_RECEIVED$")
	public void the_transport_status_of_the_cargo_is_NOT_RECEIVED()
			throws Throwable {
		theTransportStatusIs(NOT_RECEIVED);
	}

	@Given("^I request possible routes for the cargo$")
	public void I_request_possible_routes_for_the_cargo() throws Throwable {

		HttpServer server = httpserver(configurations.getPathfinderPort());
		server.get(
				and(by(uri("/pathfinder/shortestPath")),
						eq(query("origin"), origin),
						eq(query("destination"), destination),
						eq(query("arrivalDeadline"), new SimpleDateFormat(
								"yyyy-MM-dd").format(arrivalDeadline))))
				.response(json("classpath:acceptance_pathfinder_stub.json"));

		moco = new MocoHttpServer((ActualHttpServer) server);
		moco.start();

		routeCandidates = requestPossibleRoutes();

		moco.stop();
	}

	@Given("^some routes are shown$")
	public void some_routes_are_shown() throws Throwable {
		routeCandidates.andExpect(status().isOk()).andExpect(
				jsonPath("[0]").exists());
	}

	@When("^I pick up a candidate$")
	public void I_pick_up_a_candidate() throws Throwable {
		assignCargoToRoute(leg).andExpect(status().isOk());
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the cargo is assigned to the route$")
	public void the_cargo_is_assigned_to_the_route() throws Throwable {
		cargo.andExpect(jsonPath("legs").exists());
	}

	@Then("^the cargo is routed$")
	public void the_cargo_is_routed() throws Throwable {
		theRoutingStatusShouldBe(ROUTED);
	}

	@Then("^the estimated time of arrival equals to the last unloaded time of the route$")
	public void the_estimated_time_of_arrival_equals_to_the_last_unloaded_time_of_the_route()
			throws Throwable {
		cargo.andExpect(jsonPath("eta").value(
				new SimpleDateFormat("yyyy-MM-dd").format(leg.getUnloadTime())));
	}

	@Then("^the next expected handling activity is being received at the origin of the route specification$")
	public void the_next_expected_handling_activity_is_being_received_at_the_origin_of_the_route_specification()
			throws Throwable {
		theNextExpectedHandlingActivityIs(RECEIVE, origin);
	}

	private void theNextExpectedHandlingActivityIs(HandlingType type,
			String location) throws Exception {
		cargo.andExpect(jsonPath("nextExpectedHandlingActivityType").value(
				type.getCode()));
		cargo.andExpect(jsonPath("nextExpectedHandlingActivityLocation").value(
				location));
	}

	private ResultActions assignCargoToRoute(LegDto... legs) throws Exception,
			JsonProcessingException {
		return mockMvc().perform(
				post("/cargo/" + currentTrackingId()).content(
						json(new RouteCandidateDto(legs))).contentType(
						MediaType.APPLICATION_JSON)).andDo(print());
	}

	private ResultActions findCargoBy(String trackingId) throws Exception {
		return mockMvc().perform(get("/cargo/" + currentTrackingId()))
				.andDo(print()).andExpect(status().isOk());

	}

	private String aNewCargoIsRegistered() throws Exception {
		return mockMvc()
				.perform(
						put("/cargo").content(
								json(new RegisterCargoRequest(origin,
										destination, arrivalDeadline)))
								.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsString();

	}

	private ResultActions requestPossibleRoutes() throws Exception {
		return mockMvc().perform(get("/routes/" + currentTrackingId())).andDo(
				print());
	}

	private String currentTrackingId() {
		return this.trackingId;
	}

	private String json(String file) throws JsonParseException,
			JsonMappingException, IOException {
		return new String(IOUtils.toByteArray(wac.getResource(file)
				.getInputStream()), "UTF-8");
	}

	private String json(Object object) throws JsonProcessingException,
			UnsupportedEncodingException {
		return new String(new ObjectMapper().writeValueAsBytes(object), "UTF-8");
	}

	private MockMvc mockMvc() {
		return webAppContextSetup(this.wac).build();
	}

	private void theRoutingStatusShouldBe(RoutingStatus expected)
			throws Exception {
		cargo.andExpect(jsonPath("routingStatus").value(expected.getCode()));
	}

	private void theTransportStatusIs(TransportStatus expected)
			throws Exception {
		cargo.andExpect(jsonPath("transportStatus").value(expected.getCode()));
	}

	private Date aWeekLater() {
		return daysLater(7);
	}

	private Date twoDaysLater() {
		return daysLater(2);
	}

	private Date tommorrow() {
		return daysLater(1);
	}

	private Date daysLater(int i) {
		return new DateTime().withTimeAtStartOfDay().plusDays(i).toDate();
	}
}
