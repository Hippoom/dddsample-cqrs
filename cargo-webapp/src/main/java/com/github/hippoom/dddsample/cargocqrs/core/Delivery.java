package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.Date;

import org.apache.commons.lang.Validate;

/**
 * The actual transportation of the cargo, as opposed to the customer
 * requirement (RouteSpecification) and the plan (Itinerary).
 * 
 */
public class Delivery {

	private RoutingStatus routingStatus;
	private Date calculatedAt;

	/**
	 * Creates a new delivery snapshot based on the complete handling history of
	 * a cargo, as well as its route specification and itinerary.
	 * 
	 * @param routeSpecification
	 *            route specification
	 */
	static Delivery derivedFrom(RouteSpecification routeSpecification) {
		Validate.notNull(routeSpecification, "Route specification is required");
		return new Delivery(routeSpecification);
	}

	/**
	 * Internal constructor.
	 * 
	 * @param lastEvent
	 *            last event
	 * @param itinerary
	 *            itinerary
	 * @param routeSpecification
	 *            route specification
	 */
	private Delivery(RouteSpecification routeSpecification) {
		this.calculatedAt = new Date();
		this.routingStatus = calculateRoutingStatus(routeSpecification);
	}

	public RoutingStatus calculateRoutingStatus(
			RouteSpecification routeSpecification) {
		return RoutingStatus.NOT_ROUTED;
	}

	public RoutingStatus routingStatus() {
		return routingStatus;
	}
}
