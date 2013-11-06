package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.Date;

import org.apache.commons.lang.Validate;

/**
 * The actual transportation of the cargo, as opposed to the customer
 * requirement (RouteSpecification) and the plan (Itinerary).
 * 
 */
public class Delivery {
	private static final Date ETA_UNKOWN = null;

	private RoutingStatus routingStatus;
	private Date eta;
	private HandlingActivity nextExpectedHandlingActivity;

	/**
	 * Creates a new delivery snapshot based on the complete handling history of
	 * a cargo, as well as its route specification and itinerary.
	 * 
	 * @param routeSpecification
	 *            route specification
	 */
	static Delivery derivedFrom(RouteSpecification routeSpecification) {
		return new Delivery(routeSpecification);
	}

	/**
	 * Creates a new delivery snapshot to reflect changes in routing, i.e. when
	 * the route specification or the itinerary has changed but no additional
	 * handling of the cargo has been performed.
	 * 
	 * @param routeSpecification
	 *            route specification
	 * @param itinerary
	 *            itinerary
	 * @return An up to date delivery
	 */
	static Delivery derivedFrom(RouteSpecification routeSpecification,
			Itinerary itinerary) {
		return new Delivery(itinerary, routeSpecification);
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
		this(null, routeSpecification);
	}

	public Delivery(Itinerary itinerary, RouteSpecification routeSpecification) {
		Validate.notNull(routeSpecification, "Route specification is required");
		this.routingStatus = calculateRoutingStatus(itinerary,
				routeSpecification);
		this.eta = calculateEta(itinerary);
		this.nextExpectedHandlingActivity = calculateNextExpectedActivity(
				routeSpecification, itinerary);
	}

	private HandlingActivity calculateNextExpectedActivity(
			RouteSpecification routeSpecification, Itinerary itinerary) {
		return new HandlingActivity(HandlingType.RECEIVE,
				routeSpecification.getOrigin());
	}

	private RoutingStatus calculateRoutingStatus(Itinerary itinerary,
			RouteSpecification routeSpecification) {
		if (itinerary == null) {
			return RoutingStatus.NOT_ROUTED;
		} else {
			return RoutingStatus.ROUTED;
		}
	}

	private Date calculateEta(Itinerary itinerary) {
		if (onTrack()) {
			return itinerary.finalArrivalDate();
		} else {
			return ETA_UNKOWN;
		}
	}

	private boolean onTrack() {
		return routingStatus.equals(RoutingStatus.ROUTED);
	}

	public HandlingActivity nextExpectedHandlingActivity() {
		return nextExpectedHandlingActivity;
	}

	public RoutingStatus routingStatus() {
		return routingStatus;
	}

	public Date eta() {
		return eta;
	}
}
