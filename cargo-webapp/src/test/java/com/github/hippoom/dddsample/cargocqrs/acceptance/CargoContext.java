package com.github.hippoom.dddsample.cargocqrs.acceptance;

import lombok.ToString;

import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;
import com.github.hippoom.dddsample.cargocqrs.rest.RegisterHandlingEventRequest;

@ToString
public class CargoContext {

	private String trackingId;

	private CargoDto cargo;

	private int legIndex;

	private RegisterHandlingEventRequest currentHandlingEventRequest;

	public CargoContext saveCurrentTrackingId(String trackingId) {
		this.trackingId = trackingId;
		return this;
	}

	public CargoContext saveCurrent(CargoDto cargo) {
		this.cargo = cargo;
		return this;
	}

	public CargoContext saveCurrentLegIndex(int i) {
		this.legIndex = i;
		return this;
	}

	public String currentTrackingId() {
		return trackingId;
	}

	public CargoDto currentCargo() {
		return cargo;
	}

	public int currentLegIndex() {
		return legIndex;
	}

	public LegDto lastLeg() {
		final int size = currentCargo().getLegs().size();
		return currentCargo().getLegs().get(size - 1);
	}

	public LegDto currentLeg() {
		return currentCargo().getLegs().get(currentLegIndex());
	}

	public LegDto nextLeg() {
		return currentCargo().getLegs().get(currentLegIndex() + 1);
	}

	public RegisterHandlingEventRequest currentHandlingEventRequest() {
		return currentHandlingEventRequest;
	}

	public RegisterHandlingEventRequest aReceiveHandlingEvent()
			throws Throwable {

		currentHandlingEventRequest = new RegisterHandlingEventRequest();
		currentHandlingEventRequest.setTrackingId(currentTrackingId());
		currentHandlingEventRequest.setHandlingType(HandlingType.RECEIVE
				.getCode());
		currentHandlingEventRequest.setLocation(currentLeg().getFrom());
		currentHandlingEventRequest.setCompletionTime(currentLeg()
				.getLoadTime());

		return currentHandlingEventRequest;
	}

	public RegisterHandlingEventRequest aLoadHandlingEventOf(LegDto leg) {
		currentHandlingEventRequest = new RegisterHandlingEventRequest();
		currentHandlingEventRequest.setCompletionTime(leg.getLoadTime());
		currentHandlingEventRequest
				.setHandlingType(HandlingType.LOAD.getCode());
		currentHandlingEventRequest.setLocation(leg.getFrom());
		currentHandlingEventRequest.setTrackingId(currentTrackingId());
		currentHandlingEventRequest.setVoyageNumber(leg.getVoyageNumber());
		return currentHandlingEventRequest;
	}

	public RegisterHandlingEventRequest anUnloadHandlingEventOf(LegDto leg) {
		currentHandlingEventRequest = new RegisterHandlingEventRequest();
		currentHandlingEventRequest.setCompletionTime(leg.getUnloadTime());
		currentHandlingEventRequest.setHandlingType(HandlingType.UNLOAD
				.getCode());
		currentHandlingEventRequest.setLocation(leg.getTo());
		currentHandlingEventRequest.setTrackingId(currentTrackingId());
		currentHandlingEventRequest.setVoyageNumber(leg.getVoyageNumber());
		return currentHandlingEventRequest;
	}

	public RegisterHandlingEventRequest aClaimedHandlingEventOf(LegDto leg) {
		currentHandlingEventRequest = new RegisterHandlingEventRequest();
		currentHandlingEventRequest.setCompletionTime(leg.getUnloadTime());
		currentHandlingEventRequest.setHandlingType(HandlingType.CLAIM
				.getCode());
		currentHandlingEventRequest.setLocation(leg.getTo());
		currentHandlingEventRequest.setTrackingId(currentTrackingId());
		return currentHandlingEventRequest;
	}
}
