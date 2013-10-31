package com.github.hippoom.dddsample.cargocqrs.acceptance;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.rest.StatusCodes;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@WebAppConfiguration
@ContextConfiguration("classpath:acceptance.xml")
public class CargoAdminSteps {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;
	private ResultActions result;

	private UnLocode origin = new UnLocode("SHA");

	private UnLocode destination = new UnLocode("PEK");

	private DateTime arrivalDeadline = aWeekLater();

	private DateTime aWeekLater() {
		return DateTime.now().plusDays(7);
	}

	@When("^I fill the form with origin, destination and arrival deadline$")
	public void I_fill_the_form_with_origin_destination_and_arrival_deadline()
			throws Throwable {
		this.mockMvc = webAppContextSetup(this.wac).build();
		result = mockMvc.perform(
				put("/cargo").content(
						json(origin, destination, arrivalDeadline))
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print());

	}

	private String json(UnLocode origin, UnLocode destination,
			DateTime arrivalDeadline) {
		return "{" + format("origin") + ":" + format(origin.getUnlocode())
				+ ", " + format("destination") + ":"
				+ format(destination.getUnlocode()) + ", "
				+ format("arrivalDeadline") + ":" + field(arrivalDeadline)
				+ "}";
	}

	private String format(String value) {
		return "\"" + value + "\"";
	}

	private String field(DateTime value) {
		return "\"" + DateTimeFormat.forPattern("yyyy-MM-dd").print(value)
				+ "\"";
	}

	@Then("^a new cargo is registered$")
	public void a_new_cargo_is_registered() throws Throwable {
		result.andExpect(status().isOk()).andExpect(
				jsonPath("statusCode").value(StatusCodes.SUCCESS));

	}

	@Then("^the tracking id is shown for following steps$")
	public void the_tracking_id_is_shown_for_following_steps() throws Throwable {
		result.andExpect(jsonPath("trackingId").value(not(nullValue())));
	}

	@Given("^a cargo has been registered$")
	public void a_cargo_has_been_registered() throws Throwable {
		// Express the Regexp above with the code you wish you had
		throw new PendingException();
	}

	@Given("^I request possible routes for the cargo$")
	public void I_request_possible_routes_for_the_cargo() throws Throwable {
		// Express the Regexp above with the code you wish you had
		throw new PendingException();
	}

	@Given("^some routes are shown$")
	public void some_routes_are_shown() throws Throwable {
		// Express the Regexp above with the code you wish you had
		throw new PendingException();
	}

	@When("^I pick up a candidate$")
	public void I_pick_up_a_candidate() throws Throwable {
		// Express the Regexp above with the code you wish you had
		throw new PendingException();
	}

	@Then("^the route is assigned to the cargo$")
	public void the_route_is_assigned_to_the_cargo() throws Throwable {
		// Express the Regexp above with the code you wish you had
		throw new PendingException();
	}
}
