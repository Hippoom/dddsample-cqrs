package com.github.hippoom.dddsample.cargocqrs.command;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;

@Getter
public class AssignCargoToRouteCommand {
	private final TrackingId trackingId;
	private final Itinerary itinerary;

	public AssignCargoToRouteCommand(TrackingId trackingId, Itinerary itinerary) {
		this.trackingId = trackingId;
		this.itinerary = itinerary;
	}

}
