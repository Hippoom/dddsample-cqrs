package com.github.hippoom.dddsample.cargocqrs.core;

import java.text.SimpleDateFormat;

public class CannotAssignCargoToRouteException extends RuntimeException {
	private static final SimpleDateFormat SDF = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public CannotAssignCargoToRouteException(TrackingId trackingId,
			RouteSpecification routeSpecification, Itinerary itinerary) {

		super("Cannot assign cargo[" + trackingId
				+ "] with route specification[origin="
				+ routeSpecification.getOrigin().getUnlocode()
				+ ", destination="
				+ routeSpecification.getDestination().getUnlocode()
				+ ", arrivalDeadline="
				+ SDF.format(routeSpecification.getArrivalDeadline())
				+ "] to route[initialDepartureLocation="
				+ itinerary.initialDepartureLocation().getUnlocode()
				+ ", finalArrivalLocation="
				+ itinerary.finalArrivalLocation().getUnlocode()
				+ ", finalArrivalDate="
				+ SDF.format(itinerary.finalArrivalDate()) + "]");
	}
}
