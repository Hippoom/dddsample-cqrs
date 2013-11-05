package com.github.hippoom.dddsample.cargocqrs.event;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.Leg;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;
import com.github.hippoom.dddsample.cargocqrs.rest.RouteCandidateDto;

@Getter
public class CargoAssignedEvent {
	private final String trackingId;
	private final RouteCandidateDto route;

	public CargoAssignedEvent(String trackingId, RouteCandidateDto route) {
		this.trackingId = trackingId;
		this.route = route;
	}

	public CargoAssignedEvent(TrackingId trackingId, Itinerary itinerary) {
		this.trackingId = trackingId.getValue();
		this.route = toRoute(itinerary);
	}

	private RouteCandidateDto toRoute(Itinerary itinerary) {
		final List<LegDto> legs = new ArrayList<LegDto>();
		for (Leg leg : itinerary.getLegs()) {
			legs.add(new LegDto(leg.getVoyageNumber().getNumber(), leg
					.getLoadLocation().getUnlocode(), leg.getUnloadLocation()
					.getUnlocode(), leg.getLoadTime(), leg.getUnloadTime()));
		}
		return new RouteCandidateDto(legs);
	}

}
