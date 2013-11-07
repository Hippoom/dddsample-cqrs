package com.github.hippoom.dddsample.cargocqrs.event;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.Leg;
import com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;
import com.github.hippoom.dddsample.cargocqrs.rest.RouteCandidateDto;

@Getter
public class CargoAssignedEvent {
	private final String trackingId;
	private final RouteCandidateDto route;
	private final String routingStatus;

	public CargoAssignedEvent(TrackingId trackingId, Itinerary itinerary,
			RoutingStatus routingStatus) {
		this.trackingId = trackingId.getValue();
		this.route = toRoute(itinerary);
		this.routingStatus = routingStatus.getCode();
	}

	public CargoAssignedEvent(String trackingId, RouteCandidateDto route,
			String routingStatus) {
		this.trackingId = trackingId;
		this.route = route;
		this.routingStatus = routingStatus;
	}

	private RouteCandidateDto toRoute(Itinerary itinerary) {
		final List<LegDto> legs = new ArrayList<LegDto>();
		int i = 0;
		for (Leg leg : itinerary.getLegs()) {
			legs.add(new LegDto(i++, leg.getVoyageNumber().getNumber(), leg
					.getLoadLocation().getUnlocode(), leg.getUnloadLocation()
					.getUnlocode(), leg.getLoadTime(), leg.getUnloadTime()));
		}
		return new RouteCandidateDto(legs);
	}

	public Itinerary itinerary() {
		final List<Leg> legs = new ArrayList<Leg>();
		for (LegDto leg : route.getLegs()) {
			legs.add(new Leg(new VoyageNumber(leg.getVoyageNumber()),
					new UnLocode(leg.getFrom()), new UnLocode(leg.getTo()), leg
							.getLoadTime(), leg.getUnloadTime()));
		}
		return new Itinerary(legs);
	}

}
