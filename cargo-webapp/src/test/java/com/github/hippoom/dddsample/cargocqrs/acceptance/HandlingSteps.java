package com.github.hippoom.dddsample.cargocqrs.acceptance;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
import com.github.hippoom.dddsample.cargocqrs.core.TransportStatus;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;
import com.github.hippoom.dddsample.cargocqrs.rest.RegisterHandlingEventRequest;
import com.github.hippoom.dddsample.cargocqrs.rest.RouteCandidateDto;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:acceptance.xml")
public class HandlingSteps {

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

	private MockMvc mockMvc() {
		return webAppContextSetup(this.wac).build();
	}

	private CargoDto findCargoBy(String trackingId) throws Exception {
		final MvcResult result = mockMvc()
				.perform(get("/cargo/" + context.currentTrackingId()))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		return objectMapper.readValue(result.getResponse()
				.getContentAsByteArray(), CargoDto.class);
	}

	private void a_cargo_has_been_registered() throws Throwable {
		this.context.saveCurrentTrackingId(aNewCargoIsRegistered());
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

	private byte[] json(RouteCandidateDto routeCandidateDto)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsBytes(routeCandidateDto);
	}

	private String json(String file) throws JsonParseException,
			JsonMappingException, IOException {
		return new String(IOUtils.toByteArray(wac.getResource(file)
				.getInputStream()), "UTF-8");
	}

	private void assignCargoToRoute() throws Exception, JsonProcessingException {
		mockMvc()
				.perform(
						post("/cargo/" + this.context.currentTrackingId())
								.content(json(routeCandidates.get(0)))
								.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());
	}

	private void I_request_possible_routes_for_the_cargo() throws Throwable {

		HttpServer server = httpserver(10001);
		server.get(
				and(by(uri("/pathfinder/shortestPath")),
						eq(query("spec"),
								json("classpath:acceptance_route_specification.json"))))
				.response(json("classpath:acceptance_pathfinder_stub.json"));

		moco = new MocoHttpServer((ActualHttpServer) server);
		moco.start();

		final MvcResult result = mockMvc()
				.perform(get("/routes/" + this.context.currentTrackingId()))
				.andDo(print()).andExpect(status().isOk()).andReturn();

		routeCandidates = new ObjectMapper().readValue(result.getResponse()
				.getContentAsByteArray(),
				new TypeReference<List<RouteCandidateDto>>() {
				});
		moco.stop();
	}

	@Given("^a cargo has been routed$")
	public void a_cargo_has_been_routed() throws Throwable {
		context.saveCurrentTrackingId(aNewCargoIsRegistered());

		I_request_possible_routes_for_the_cargo();
		assignCargoToRoute();
		this.context.saveCurrent(findCargoBy(context.currentTrackingId()));
	}

	private void registerHandlingEvent(RegisterHandlingEventRequest request)
			throws Exception, Throwable {
		ObjectMapper objectMapper = new ObjectMapper();
		mockMvc()
				.perform(
						put("/handlingevent/").content(
								objectMapper.writeValueAsBytes(request))
								.contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk());
	}

	@Given("^a cargo arrives at the first leg's origin$")
	public void a_cargo_arrives_at_the_first_leg_s_origin() throws Throwable {
		a_cargo_has_been_registered();
		a_cargo_has_been_routed();
	}

	@When("^I register an 'RECEIVE' handling event$")
	public void I_register_an_RECEIVE_handling_event() throws Throwable {
		registerHandlingEvent(context.aReceiveHandlingEvent());
		this.context.saveCurrent(findCargoBy(context.currentTrackingId()));
	}

	@Then("^the next expected handling activity is calculated as loaded onto the leg's voyage$")
	public void the_next_expected_handling_activity_is_calculated_as_loaded_onto_the_leg_s_voyage()
			throws Throwable {
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityLocation(), equalTo(context
				.currentLeg().getFrom()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityVoyageNumber(), equalTo(context
				.currentLeg().getVoyageNumber()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.LOAD.getCode()));
	}

	@Given("^a cargo is received$")
	public void a_cargo_is_received() throws Throwable {
		a_cargo_arrives_at_the_first_leg_s_origin();
	}

	@When("^I register an 'LOAD' handling event$")
	public void I_register_an_LOAD_handling_event() throws Throwable {
		registerHandlingEvent(context
				.aLoadHandlingEventOf(context.currentLeg()));
		this.context.saveCurrent(findCargoBy(context.currentTrackingId()));
	}

	@Then("^the transport status of the cargo is marked as ONBOARD_CARRIER$")
	public void the_transport_status_of_the_cargo_is_marked_as_ONBOARD_CARRIER()
			throws Throwable {
		assertThat(this.context.currentCargo().getTransportStatus(),
				equalTo(TransportStatus.ONBOARD_CARRIER.getCode()));
	}

	@Then("^the current voyage of the cargo is updated$")
	public void the_current_voyage_of_the_cargo_is_updated() throws Throwable {
		assertThat(this.context.currentCargo().getCurrentVoyageNumber(),
				equalTo(this.context.currentHandlingEventRequest()
						.getVoyageNumber()));
	}

	@Then("^the next expected handling activity is calculated as unloaded at the leg's destination$")
	public void the_next_expected_handling_activity_is_calculated_as_unloaded_at_the_leg_s_destination()
			throws Throwable {
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityLocation(), equalTo(context
				.currentLeg().getTo()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityVoyageNumber(), equalTo(context
				.currentLeg().getVoyageNumber()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.UNLOAD.getCode()));
	}

	@Given("^a cargo is assigned to an itinerary with multiple legs$")
	public void a_cargo_is_assigned_to_an_itinerary_with_multiple_legs()
			throws Throwable {

	}

	@Given("^the cargo arrives at the destination of the first leg$")
	public void the_cargo_arrives_at_the_destination_of_the_first_leg()
			throws Throwable {
		a_cargo_is_received();
		this.context.saveCurrentLegIndex(0);
	}

	@When("^I register an 'UNLOAD' handling event$")
	public void I_register_an_UNLOAD_handling_event() throws Throwable {
		registerHandlingEvent(context.anUnloadHandlingEventOf(context
				.currentLeg()));
		this.context.saveCurrent(findCargoBy(context.currentTrackingId()));
	}

	@Then("^the next expected handling activity is calculated as loaded onto the next leg's voyage$")
	public void the_next_expected_handling_activity_is_calculated_as_loaded_onto_the_next_leg_s_voyage()
			throws Throwable {
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityLocation(), equalTo(context
				.nextLeg().getFrom()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityVoyageNumber(), equalTo(context
				.nextLeg().getVoyageNumber()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.LOAD.getCode()));
	}

	@Given("^a cargo arrives at the final destination$")
	public void a_cargo_arrives_at_the_destination() throws Throwable {
		a_cargo_is_received();
		this.context.saveCurrentLegIndex(1);
	}

	@Then("^the transport status of the cargo is marked as IN_PORT$")
	public void the_transport_status_of_the_cargo_is_marked_as_IN_PORT()
			throws Throwable {
		assertThat(this.context.currentCargo().getTransportStatus(),
				equalTo(TransportStatus.IN_PORT.getCode()));
	}

	@Then("^the last known location of the cargo is updated$")
	public void the_last_known_location_of_the_cargo_is_updated()
			throws Throwable {
		assertThat(this.context.currentCargo().getLastKnownLocation(),
				equalTo(this.context.currentHandlingEventRequest()
						.getLocation()));
	}

	@Then("^the cargo is marked as not currently on any voyage$")
	public void the_cargo_is_marked_as_not_currently_on_any_voyage()
			throws Throwable {
		assertThat(this.context.currentCargo().getCurrentVoyageNumber(),
				is(nullValue()));
	}

	@Then("^the cargo is marked as unloaded at the destination$")
	public void the_cargo_is_marked_as_unloaded_at_the_destination()
			throws Throwable {
		assertThat(this.context.currentCargo()
				.getUnloadedAtDestinationIndicator(), equalTo("1"));
	}

	@Then("^the next expected handling activity is calculated as claimed at the destination$")
	public void the_next_expected_handling_activity_is_calculated_as_claimed_at_the_destination()
			throws Throwable {
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityLocation(), equalTo(context
				.lastLeg().getTo()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityVoyageNumber(), is(nullValue()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.CLAIM.getCode()));
	}

	@Given("^a cargo is unloaded at the destination$")
	public void a_cargo_has_been_unloaded_at_the_destination() throws Throwable {
		a_cargo_arrives_at_the_destination();
	}

	@When("^I register a CLAIM handling event$")
	public void I_register_a_handling_event_of_which_type_is_CLAIM()
			throws Throwable {
		registerHandlingEvent(context.aClaimedHandlingEventOf(this.context
				.lastLeg()));
		this.context.saveCurrent(findCargoBy(context.currentTrackingId()));
	}

	@Then("^the transport status of the cargo is marked as CLAIMED$")
	public void the_transport_status_of_the_cargo_is_CLAIMED() throws Throwable {
		assertThat(this.context.currentCargo().getTransportStatus(),
				equalTo(TransportStatus.CLAIMED.getCode()));
	}

	@Then("^the next expected handling activity is calculated as none$")
	public void the_next_expected_handling_activity_is_none() throws Throwable {
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityLocation(), is(nullValue()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityType(), is(nullValue()));
		assertThat(this.context.currentCargo()
				.getNextExpectedHandlingActivityVoyageNumber(), is(nullValue()));
	}

}
