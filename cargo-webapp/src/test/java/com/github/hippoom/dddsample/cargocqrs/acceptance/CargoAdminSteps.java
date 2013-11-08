package com.github.hippoom.dddsample.cargocqrs.acceptance;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus;
import com.github.hippoom.dddsample.cargocqrs.core.TransportStatus;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;
import com.github.hippoom.dddsample.cargocqrs.rest.RouteCandidateDto;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:acceptance.xml")
public class CargoAdminSteps {

	@Autowired
	private WebApplicationContext wac;

	private MocoHttpServer moco;
	@Autowired
	private CargoContext context;

	private List<RouteCandidateDto> routeCandidates;

	@After
	public void printContext() {
		System.err.println(context);
	}

	@When("^I fill the form with origin, destination and arrival deadline$")
	public void I_fill_the_form_with_origin_destination_and_arrival_deadline()
			throws Throwable {
		context.saveCurrentTrackingId(aNewCargoIsRegistered());

	}

	private String aNewCargoIsRegistered() throws Exception {
		final MvcResult result = mockMvc()
				.perform(
						put("/cargo")
								.content(
										json("classpath:acceptance_route_specification.json"))
								.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		return new ObjectMapper().readValue(result.getResponse()
				.getContentAsByteArray(), String.class);

	}

	private String json(String file) throws JsonParseException,
			JsonMappingException, IOException {
		return new String(IOUtils.toByteArray(wac.getResource(file)
				.getInputStream()), "UTF-8");
	}

	private MockMvc mockMvc() {
		return webAppContextSetup(this.wac).build();
	}

	@Then("^a new cargo is registered$")
	public void a_new_cargo_is_registered() throws Throwable {
		this.context.saveCurrent(findCargoBy(getCurrentTrackingId()));
		assertThat(this.context.currentCargo(), not(nullValue()));
	}

	@Then("^the cargo is not routed$")
	public void the_cargo_is_not_routed() throws Throwable {
		assertThat(this.context.currentCargo().getRoutingStatus(),
				equalTo(RoutingStatus.NOT_ROUTED.getCode()));
	}

	@Then("^the tracking id is shown for following steps$")
	public void the_tracking_id_is_shown_for_following_steps() throws Throwable {
		assertThat(context.currentTrackingId(), not(nullValue()));
	}

	@Given("^a cargo has been registered$")
	public void a_cargo_has_been_registered() throws Throwable {
		this.context.saveCurrentTrackingId(aNewCargoIsRegistered());
	}

	@Given("^I request possible routes for the cargo$")
	public void I_request_possible_routes_for_the_cargo() throws Throwable {

		HttpServer server = httpserver(10001);
		server.get(
				and(by(uri("/pathfinder/shortestPath")),
						eq(query("spec"),
								json("classpath:acceptance_route_specification.json"))))
				.response(json("classpath:acceptance_pathfinder_stub.json"));

		moco = new MocoHttpServer((ActualHttpServer) server);
		moco.start();

		final MvcResult result = mockMvc()
				.perform(get("/routes/" + getCurrentTrackingId()))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		routeCandidates = new ObjectMapper().readValue(result.getResponse()
				.getContentAsByteArray(),
				new TypeReference<List<RouteCandidateDto>>() {
				});
		moco.stop();
	}

	private String getCurrentTrackingId() {
		return this.context.currentTrackingId();
	}

	@Given("^some routes are shown$")
	public void some_routes_are_shown() throws Throwable {
		assertThat(routeCandidates, hasSize(greaterThan(1)));
	}

	@When("^I pick up a candidate$")
	public void I_pick_up_a_candidate() throws Throwable {
		assignCargoToRoute();

	}

	private void assignCargoToRoute() throws Exception, JsonProcessingException {
		mockMvc()
				.perform(
						post("/cargo/" + getCurrentTrackingId()).content(
								json(routeCandidates.get(0))).contentType(
								MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(status().isOk());
	}

	private byte[] json(RouteCandidateDto routeCandidateDto)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsBytes(routeCandidateDto);
	}

	@Then("^the cargo is assigned to the route$")
	public void the_cargo_is_assigned_to_the_route() throws Throwable {
		this.context.saveCurrent(findCargoBy(context.currentTrackingId()));
		assertThat(this.context.currentCargo().getLegs(),
				equalTo(routeCandidates.get(0).getLegs()));
	}

	@Then("^the cargo is routed$")
	public void the_cargo_is_routed() throws Throwable {
		assertThat(this.context.currentCargo().getRoutingStatus(),
				equalTo(RoutingStatus.ROUTED.getCode()));
	}

	@Then("^the estimated time of arrival equals to the last unloaded time of the route$")
	public void the_estimated_time_of_arrival_equals_to_the_last_unloaded_time_of_the_route()
			throws Throwable {
		List<LegDto> legs = routeCandidates.get(0).getLegs();
		assertThat(this.context.currentCargo().getEta(),
				equalTo(legs.get(legs.size() - 1).getUnloadTime()));
	}

	@Then("^the transport status of the cargo is NOT_RECEIVED$")
	public void the_transport_status_of_the_cargo_is_NOT_RECEIVED()
			throws Throwable {
		assertThat(this.context.currentCargo().getTransportStatus(),
				equalTo(TransportStatus.NOT_RECEIVED.getCode()));
	}

	@Then("^the next expected handling activity is being received at the origin of the route specification$")
	public void the_next_expected_handling_activity_is_being_received_at_the_origin_of_the_route_specification()
			throws Throwable {
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.RECEIVE.getCode()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityLocation(),
				equalTo(this.context.currentCargo().getOriginUnlocode()));
	}

	private CargoDto findCargoBy(String trackingId) throws Exception {
		final MvcResult result = mockMvc()
				.perform(get("/cargo/" + getCurrentTrackingId()))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		return objectMapper.readValue(result.getResponse()
				.getContentAsByteArray(), CargoDto.class);
	}

}
