package com.github.hippoom.dddsample.cargocqrs.acceptance;

import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.at;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.currentVoyageNumberOf;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.from;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.isNone;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.lastKnownLocationOf;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.onto;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.shoulbeMarkedAsUnloadedAtDestination;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.shouldBe;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.the;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.theNextExpectedHandlingActivityOf;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.theTransportStatusOf;
import static com.github.hippoom.dddsample.cargocqrs.acceptance.CargoAssertions.thereShouldBeNoMoreExpectedHandlingActivityOf;
import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.CLAIM;
import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.LOAD;
import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.RECEIVE;
import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.UNLOAD;
import static com.github.hippoom.dddsample.cargocqrs.core.TransportStatus.IN_PORT;
import static com.github.hippoom.dddsample.cargocqrs.core.TransportStatus.ONBOARD_CARRIER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.TransportStatus;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:acceptance.xml")
public class HandlingSteps {

	@Autowired
	private WebApplicationContext wac;

	private String trackingId;

	private ResultActions cargo;

	private String locationOfEvent;

	private String voyageNumberOfEvent;
	/* test data starts */
	private String origin = "CNSHA";

	private String destination = "CNPEK";

	private Date arrivalDeadline = aWeekLater();

	private LegDto firstLeg = new LegDto(0, "CM001", "CNSHA", "CNTAN",
			tommorrow(), twoDaysLater());
	private LegDto lastLeg = new LegDto(0, "CM002", "CNTAN", "CNPEK",
			twoDaysLater(), sixDaysLater());

	/* test data ends */

	@Given("^a cargo arrives at the first leg's origin$")
	public void a_cargo_arrives_at_the_first_leg_s_origin() throws Throwable {
		this.trackingId = aNewCargoIsRegistered();
		theCargoIsRouted();
	}

	@When("^I register an 'RECEIVE' handling event$")
	public void I_register_an_RECEIVE_handling_event() throws Throwable {
		registerHandlingEventWith(RECEIVE, firstLeg.getFrom(),
				firstLeg.getVoyageNumber(), firstLeg.getLoadTime());
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the next expected handling activity is calculated as loaded onto the leg's voyage$")
	public void the_next_expected_handling_activity_is_calculated_as_loaded_onto_the_leg_s_voyage()
			throws Throwable {
		theNextExpectedHandlingActivityOf(cargo, shouldBe(LOAD),
				onto(firstLeg.getVoyageNumber()), at(firstLeg.getFrom()));
	}

	@Given("^a cargo is received$")
	public void a_cargo_is_received() throws Throwable {
		this.trackingId = aNewCargoIsRegistered();
		theCargoIsRouted();
	}

	@When("^I register an 'LOAD' handling event$")
	public void I_register_an_LOAD_handling_event() throws Throwable {
		registerHandlingEventWith(LOAD, firstLeg.getFrom(),
				firstLeg.getVoyageNumber(), firstLeg.getLoadTime());
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the transport status of the cargo is marked as ONBOARD_CARRIER$")
	public void the_transport_status_of_the_cargo_is_marked_as_ONBOARD_CARRIER()
			throws Throwable {
		theTransportStatusOf(cargo, shouldBe(ONBOARD_CARRIER));
	}

	@Then("^the current voyage of the cargo is updated$")
	public void the_current_voyage_of_the_cargo_is_updated() throws Throwable {
		currentVoyageNumberOf(cargo, shouldBe(voyageNumberOfEvent));
	}

	@Then("^the next expected handling activity is calculated as unloaded at the leg's destination$")
	public void the_next_expected_handling_activity_is_calculated_as_unloaded_at_the_leg_s_destination()
			throws Throwable {
		theNextExpectedHandlingActivityOf(cargo, shouldBe(UNLOAD),
				from(firstLeg.getVoyageNumber()), at(firstLeg.getTo()));
	}

	@Given("^a cargo is assigned to an itinerary with multiple legs$")
	public void a_cargo_is_assigned_to_an_itinerary_with_multiple_legs()
			throws Throwable {
		this.trackingId = aNewCargoIsRegistered();
		theCargoIsRouted();

	}

	@Given("^the cargo arrives at the destination of the first leg$")
	public void the_cargo_arrives_at_the_destination_of_the_first_leg()
			throws Throwable {
	}

	@When("^I register an 'UNLOAD' handling event for the first leg$")
	public void I_register_an_UNLOAD_handling_event() throws Throwable {
		registerHandlingEventWith(UNLOAD, firstLeg.getTo(),
				firstLeg.getVoyageNumber(), firstLeg.getUnloadTime());
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the next expected handling activity is calculated as loaded onto the next leg's voyage$")
	public void the_next_expected_handling_activity_is_calculated_as_loaded_onto_the_next_leg_s_voyage()
			throws Throwable {
		theNextExpectedHandlingActivityOf(cargo, shouldBe(LOAD),
				onto(lastLeg.getVoyageNumber()), at(lastLeg.getFrom()));
	}

	@Given("^a cargo arrives at the final destination$")
	public void a_cargo_arrives_at_the_destination() throws Throwable {
		this.trackingId = aNewCargoIsRegistered();
		theCargoIsRouted();
	}

	@When("^I register an 'UNLOAD' handling event for the last leg$")
	public void I_register_an_UNLOAD_handling_event_last() throws Throwable {
		registerHandlingEventWith(UNLOAD, lastLeg.getTo(),
				lastLeg.getVoyageNumber(), lastLeg.getUnloadTime());
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the transport status of the cargo is marked as IN_PORT$")
	public void the_transport_status_of_the_cargo_is_marked_as_IN_PORT()
			throws Throwable {
		theTransportStatusOf(cargo, shouldBe(IN_PORT));
	}

	@Then("^the last known location of the cargo is updated$")
	public void the_last_known_location_of_the_cargo_is_updated()
			throws Throwable {
		lastKnownLocationOf(cargo, shouldBe(at(locationOfEvent)));
	}

	@Then("^the cargo is marked as not currently on any voyage$")
	public void the_cargo_is_marked_as_not_currently_on_any_voyage()
			throws Throwable {
		currentVoyageNumberOf(cargo, isNone());
	}

	@Then("^the cargo is marked as unloaded at the destination$")
	public void the_cargo_is_marked_as_unloaded_at_the_destination()
			throws Throwable {
		the(cargo, shoulbeMarkedAsUnloadedAtDestination());
	}

	@Then("^the next expected handling activity is calculated as claimed at the destination$")
	public void the_next_expected_handling_activity_is_calculated_as_claimed_at_the_destination()
			throws Throwable {
		theNextExpectedHandlingActivityOf(cargo, shouldBe(CLAIM),
				at(destination));
	}

	@Given("^a cargo is unloaded at the destination$")
	public void a_cargo_has_been_unloaded_at_the_destination() throws Throwable {
		this.trackingId = aNewCargoIsRegistered();
		theCargoIsRouted();
	}

	@When("^I register a CLAIM handling event$")
	public void I_register_a_handling_event_of_which_type_is_CLAIM()
			throws Throwable {
		registerHandlingEventWith(CLAIM, lastLeg.getTo(), null,
				lastLeg.getUnloadTime());
		this.cargo = findCargoBy(trackingId);
	}

	@Then("^the transport status of the cargo is marked as CLAIMED$")
	public void the_transport_status_of_the_cargo_is_CLAIMED() throws Throwable {
		theTransportStatusOf(cargo, shouldBe(TransportStatus.CLAIMED));
	}

	@Then("^the next expected handling activity is calculated as none$")
	public void the_next_expected_handling_activity_is_none() throws Throwable {
		thereShouldBeNoMoreExpectedHandlingActivityOf(cargo);
	}

	private Date aWeekLater() {
		return daysLater(7);
	}

	private Date sixDaysLater() {
		return daysLater(6);
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

	private ResultActions findCargoBy(String trackingId) throws Exception {
		return new CargoFixture(wac).findCargoBy(trackingId);
	}

	private String aNewCargoIsRegistered() throws Exception {
		return new CargoFixture(wac).aNewCargoIsRegisteredWith(origin,
				destination, arrivalDeadline);

	}

	private void theCargoIsRouted() throws Exception, JsonProcessingException {
		new CargoFixture(wac).assignCargoToRoute(trackingId, firstLeg, lastLeg)
				.andExpect(status().isOk());
	}

	private void registerHandlingEventWith(HandlingType type, String location,
			String voyageNumber, Date completionTime) throws Exception,
			Throwable {
		this.locationOfEvent = location;
		this.voyageNumberOfEvent = voyageNumber;
		new CargoFixture(wac).aHandlingEventRegistered(trackingId, type,
				location, voyageNumber, completionTime).andExpect(
				status().isOk());
	}
}
