package com.github.hippoom.dddsample.cargocqrs.acceptance;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.ResultActions;

import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus;
import com.github.hippoom.dddsample.cargocqrs.core.TransportStatus;

public class CargoAssertions {
	public static void theTransportStatusOf(ResultActions cargo,
			String transportStatus) throws Exception {
		cargo.andExpect(jsonPath("transportStatus").value(transportStatus));
	}

	public static void currentVoyageNumberOf(ResultActions cargo,
			String expected) throws Exception {
		cargo.andExpect(jsonPath("currentVoyageNumber").value(expected));
	}

	public static void theRoutingStatusOf(ResultActions cargo, String expected)
			throws Exception {
		cargo.andExpect(jsonPath("routingStatus").value(expected));
	}

	public static void theNextExpectedHandlingActivityOf(ResultActions cargo,
			String type, String location) throws Exception {
		theNextExpectedHandlingActivityOf(cargo, type, "", location);
	}

	public static void theNextExpectedHandlingActivityOf(ResultActions cargo,
			String type, String voyage, String location) throws Exception {
		cargo.andExpect(jsonPath("nextExpectedHandlingActivityType")
				.value(type));
		cargo.andExpect(jsonPath("nextExpectedHandlingActivityVoyageNumber")
				.value(voyage));
		cargo.andExpect(jsonPath("nextExpectedHandlingActivityLocation").value(
				location));
	}

	public static void the(ResultActions cargo,
			boolean isUnloadedAtTheDestination) throws Exception {
		cargo.andExpect(jsonPath("unloadedAtDestinationIndicator").value(
				isUnloadedAtTheDestination ? "1" : null));
	}

	public static void thereShouldBeNoMoreExpectedHandlingActivityOf(
			ResultActions cargo) throws Exception {
		theNextExpectedHandlingActivityOf(cargo, null, null, null);
	}

	public static void lastKnownLocationOf(ResultActions cargo, String expected)
			throws Exception {
		cargo.andExpect(jsonPath("lastKnownLocation").value(expected));
	}

	public static boolean shoulbeMarkedAsUnloadedAtDestination() {
		return true;
	}

	public static String shouldBe(RoutingStatus expected) {
		return expected.getCode();
	}

	public static String shouldBe(TransportStatus transportStatus) {
		return transportStatus.getCode();
	}

	public static String shouldBe(HandlingType expected) {
		return expected.getCode();
	}

	public static String shouldBe(String expected) {
		return expected;
	}

	public static String at(String expected) {
		return expected;
	}

	public static String onto(String expected) {
		return expected;
	}

	public static String from(String expected) {
		return expected;
	}

	public static String isNone() {
		return "";
	}
}
