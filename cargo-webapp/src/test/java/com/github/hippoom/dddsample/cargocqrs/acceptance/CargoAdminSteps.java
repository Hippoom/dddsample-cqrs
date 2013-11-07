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
import static org.hamcrest.Matchers.is;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
import com.github.hippoom.dddsample.cargocqrs.rest.RegisterHandlingEventRequest;
import com.github.hippoom.dddsample.cargocqrs.rest.RouteCandidateDto;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:acceptance.xml")
public class CargoAdminSteps implements ApplicationContextAware {

	@Autowired
	private WebApplicationContext wac;

	private MocoHttpServer moco;

	private ApplicationContext applicationContext;

	private String trackingId;

	private CargoDto cargo;

	private List<RouteCandidateDto> routeCandidates;

	private RegisterHandlingEventRequest request;

	@When("^I fill the form with origin, destination and arrival deadline$")
	public void I_fill_the_form_with_origin_destination_and_arrival_deadline()
			throws Throwable {
		this.trackingId = aNewCargoIsRegistered();

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
		return new String(IOUtils.toByteArray(applicationContext.getResource(
				file).getInputStream()), "UTF-8");
	}

	private MockMvc mockMvc() {
		return webAppContextSetup(this.wac).build();
	}

	@Then("^a new cargo is registered$")
	public void a_new_cargo_is_registered() throws Throwable {
		this.cargo = findCargoBy(this.trackingId);
		assertThat(cargo, not(nullValue()));
	}

	@Then("^the cargo is not routed$")
	public void the_cargo_is_not_routed() throws Throwable {
		assertThat(cargo.getRoutingStatus(),
				equalTo(RoutingStatus.NOT_ROUTED.getCode()));
	}

	@Then("^the tracking id is shown for following steps$")
	public void the_tracking_id_is_shown_for_following_steps() throws Throwable {
		assertThat(trackingId, not(nullValue()));
	}

	@Given("^a cargo has been registered$")
	public void a_cargo_has_been_registered() throws Throwable {
		this.trackingId = aNewCargoIsRegistered();
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
				.perform(get("/routes/" + this.trackingId)).andDo(print())
				.andExpect(status().isOk()).andReturn();

		routeCandidates = new ObjectMapper().readValue(result.getResponse()
				.getContentAsByteArray(),
				new TypeReference<List<RouteCandidateDto>>() {
				});
		moco.stop();
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
						post("/cargo/" + this.trackingId).content(
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
		this.cargo = findCargoBy(this.trackingId);
		assertThat(cargo.getLegs(), equalTo(routeCandidates.get(0).getLegs()));
	}

	@Then("^the cargo is routed$")
	public void the_cargo_is_routed() throws Throwable {
		assertThat(cargo.getRoutingStatus(),
				equalTo(RoutingStatus.ROUTED.getCode()));
	}

	@Then("^the estimated time of arrival equals to the last unloaded time of the route$")
	public void the_estimated_time_of_arrival_equals_to_the_last_unloaded_time_of_the_route()
			throws Throwable {
		List<LegDto> legs = routeCandidates.get(0).getLegs();
		assertThat(cargo.getEta(), equalTo(legs.get(legs.size() - 1)
				.getUnloadTime()));
	}

	@Then("^the transport status of the cargo is NOT_RECEIVED$")
	public void the_transport_status_of_the_cargo_is_NOT_RECEIVED()
			throws Throwable {
		assertThat(cargo.getTransportStatus(),
				equalTo(TransportStatus.NOT_RECEIVED.getCode()));
	}

	@Then("^the next expected handling activity is being received at the origin of the route specification$")
	public void the_next_expected_handling_activity_is_being_received_at_the_origin_of_the_route_specification()
			throws Throwable {
		assertThat(cargo.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.RECEIVE.getCode()));
		assertThat(cargo.getNextExpectedHandlingActivityLocation(),
				equalTo(cargo.getOriginUnlocode()));
	}

	private CargoDto findCargoBy(String trackingId) throws Exception {
		final MvcResult result = mockMvc()
				.perform(get("/cargo/" + this.trackingId)).andDo(print())
				.andExpect(status().isOk()).andReturn();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		return objectMapper.readValue(result.getResponse()
				.getContentAsByteArray(), CargoDto.class);
	}

	@Given("^a cargo has been routed$")
	public void a_cargo_has_been_routed() throws Throwable {
		this.trackingId = aNewCargoIsRegistered();

		I_request_possible_routes_for_the_cargo();
		assignCargoToRoute();
		this.cargo = findCargoBy(trackingId);
	}

	@When("^I register a new handling event of which type is RECEIVE$")
	public void I_register_a_new_handling_event_of_which_type_is_RECEIVE()
			throws Throwable {
		registerHandlingEvent(firstHandlingEventOf(cargo));
		this.cargo = findCargoBy(trackingId);
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

	private RegisterHandlingEventRequest firstHandlingEventOf(CargoDto cargo)
			throws Throwable {

		request = new RegisterHandlingEventRequest();
		request.setTrackingId(cargo.getTrackingId());
		request.setHandlingType(cargo.getNextExpectedHandlingActivityType());
		request.setLocation(cargo.getNextExpectedHandlingActivityLocation());
		request.setVoyageNumber(cargo.getLegs().get(0).getVoyageNumber());
		request.setCompletionTime(cargo.getLegs().get(0).getUnloadTime());

		return request;
	}

	@Then("^the transport status of the cargo is IN_PORT$")
	public void the_transport_status_of_the_cargo_is_IN_PORT() throws Throwable {

		assertThat(cargo.getTransportStatus(),
				equalTo(TransportStatus.IN_PORT.getCode()));
	}

	@Then("^the last known location of the cargo is updated as the location of the handling event$")
	public void the_last_known_location_of_the_cargo_is_updated_as_the_location_of_the_handling_event()
			throws Throwable {
		assertThat(cargo.getTransportStatus(),
				equalTo(TransportStatus.IN_PORT.getCode()));
	}

	@Then("^the current voyage of the cargo is updated as the voyage of the handling event$")
	public void the_current_voyage_of_the_cargo_is_updated_as_the_voyage_of_the_handling_event()
			throws Throwable {
		assertThat(cargo.getLastKnownLocation(), equalTo(request.getLocation()));
	}

	@Then("^the next expected handling activity is being loaded to the leg's voyage$")
	public void the_next_expected_handling_activity_is_being_loaded_to_the_legs_voyage()
			throws Throwable {
		assertThat(cargo.getNextExpectedHandlingActivityLocation(),
				equalTo(cargo.getLegs().get(0).getFrom()));
		assertThat(cargo.getNextExpectedHandlingActivityVoyageNumber(),
				equalTo(cargo.getLegs().get(0).getVoyageNumber()));
		assertThat(cargo.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.LOAD.getCode()));
	}

	@Given("^the cargo is received$")
	public void the_cargo_is_received() throws Throwable {
		I_register_a_new_handling_event_of_which_type_is_RECEIVE();

	}

	private RegisterHandlingEventRequest aLoadHandlingEventOf(CargoDto cargo) {
		request = new RegisterHandlingEventRequest();
		LegDto firstLeg = cargo.getLegs().get(0);
		request.setCompletionTime(firstLeg.getLoadTime());
		request.setHandlingType(HandlingType.LOAD.getCode());
		request.setLocation(firstLeg.getFrom());
		request.setTrackingId(cargo.getTrackingId());
		request.setVoyageNumber(firstLeg.getVoyageNumber());
		return request;
	}

	private RegisterHandlingEventRequest lastLoadHandlingEventOf(CargoDto cargo) {
		request = new RegisterHandlingEventRequest();
		LegDto leg = cargo.getLegs().get(1);
		request.setCompletionTime(leg.getLoadTime());
		request.setHandlingType(HandlingType.LOAD.getCode());
		request.setLocation(leg.getFrom());
		request.setTrackingId(cargo.getTrackingId());
		request.setVoyageNumber(leg.getVoyageNumber());
		return request;
	}

	private RegisterHandlingEventRequest anUnloadHandlingEventOf(CargoDto cargo) {
		request = new RegisterHandlingEventRequest();
		LegDto firstLeg = cargo.getLegs().get(0);
		request.setCompletionTime(firstLeg.getUnloadTime());
		request.setHandlingType(HandlingType.UNLOAD.getCode());
		request.setLocation(firstLeg.getTo());
		request.setTrackingId(cargo.getTrackingId());
		request.setVoyageNumber(firstLeg.getVoyageNumber());
		return request;
	}

	private RegisterHandlingEventRequest lastUnloadHandlingEventOf(
			CargoDto cargo) {
		request = new RegisterHandlingEventRequest();
		LegDto firstLeg = cargo.getLegs().get(1);
		request.setCompletionTime(firstLeg.getUnloadTime());
		request.setHandlingType(HandlingType.UNLOAD.getCode());
		request.setLocation(firstLeg.getTo());
		request.setTrackingId(cargo.getTrackingId());
		request.setVoyageNumber(firstLeg.getVoyageNumber());
		return request;
	}

	private RegisterHandlingEventRequest claimedHandlingEventOf(CargoDto cargo) {
		request = new RegisterHandlingEventRequest();
		LegDto firstLeg = cargo.getLegs().get(1);
		request.setCompletionTime(firstLeg.getUnloadTime());
		request.setHandlingType(HandlingType.CLAIM.getCode());
		request.setLocation(firstLeg.getTo());
		request.setTrackingId(cargo.getTrackingId());
		return request;
	}

	@When("^I register a new handling event of which type is LOAD$")
	public void I_register_a_new_handling_event_of_which_type_is_LOAD()
			throws Throwable {
		registerHandlingEvent(aLoadHandlingEventOf(cargo));
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the transport status of the cargo is ONBOARD_CARRIER$")
	public void the_transport_status_of_the_cargo_is_ONBOARD_CARRIER()
			throws Throwable {
		assertThat(cargo.getTransportStatus(),
				equalTo(TransportStatus.ONBOARD_CARRIER.getCode()));
	}

	@Then("^the next expected handling activity is being unloaded to the leg's unload location$")
	public void the_next_expected_handling_activity_is_being_unloaded_to_the_leg_s_unload_location()
			throws Throwable {
		assertThat(cargo.getNextExpectedHandlingActivityLocation(),
				equalTo(cargo.getLegs().get(0).getTo()));
		assertThat(cargo.getNextExpectedHandlingActivityVoyageNumber(),
				equalTo(cargo.getLegs().get(0).getVoyageNumber()));
		assertThat(cargo.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.UNLOAD.getCode()));

	}

	@When("^I register a new handling event of which type is UNLOAD$")
	public void I_register_a_new_handling_event_of_which_type_is_UNLOAD()
			throws Throwable {
		registerHandlingEvent(anUnloadHandlingEventOf(cargo));
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the next expected handling activity is being loaded to the next leg's voyage$")
	public void the_next_expected_handling_activity_is_being_loaded_to_the_next_leg_s_voyage()
			throws Throwable {
		assertThat(cargo.getNextExpectedHandlingActivityLocation(),
				equalTo(cargo.getLegs().get(1).getFrom()));
		assertThat(cargo.getNextExpectedHandlingActivityVoyageNumber(),
				equalTo(cargo.getLegs().get(1).getVoyageNumber()));
		assertThat(cargo.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.LOAD.getCode()));
	}

	@When("^I register the last handling event of which type is UNLOAD$")
	public void I_register_the_last_handling_event_of_which_type_is_UNLOAD()
			throws Throwable {
		I_register_a_new_handling_event_of_which_type_is_UNLOAD();
		registerHandlingEvent(lastLoadHandlingEventOf(cargo));
		registerHandlingEvent(lastUnloadHandlingEventOf(cargo));
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the next expected handling activity is claimed at the destination$")
	public void the_next_expected_handling_activity_is_claimed_at_the_destination()
			throws Throwable {
		assertThat(cargo.getNextExpectedHandlingActivityLocation(),
				equalTo(cargo.getLegs().get(1).getTo()));
		assertThat(cargo.getNextExpectedHandlingActivityVoyageNumber(),
				is(nullValue()));
		assertThat(cargo.getNextExpectedHandlingActivityType(),
				equalTo(HandlingType.CLAIM.getCode()));
	}

	@Then("^the cargo is unloaded at the destination$")
	public void the_cargo_is_unloaded_at_the_destination() throws Throwable {
		assertThat(cargo.getUnloadedAtDestinationIndicator(), equalTo("1"));
	}

	@Given("^the cargo has been unloaded at the destination$")
	public void the_cargo_has_been_unloaded_at_the_destination()
			throws Throwable {
		I_register_a_new_handling_event_of_which_type_is_RECEIVE();
		I_register_the_last_handling_event_of_which_type_is_UNLOAD();
	}

	@When("^I register a handling event of which type is CLAIM$")
	public void I_register_a_handling_event_of_which_type_is_CLAIM()
			throws Throwable {
		registerHandlingEvent(claimedHandlingEventOf(cargo));
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the transport status of the cargo is CLAIMED$")
	public void the_transport_status_of_the_cargo_is_CLAIMED() throws Throwable {
		assertThat(cargo.getTransportStatus(),
				equalTo(TransportStatus.CLAIMED.getCode()));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
