package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.Validate;

/**
 * The actual transportation of the cargo, as opposed to the customer
 * requirement (RouteSpecification) and the plan (Itinerary).
 * 
 */
public class Delivery {
	private static final Date ETA_UNKOWN = null;

	private RoutingStatus routingStatus;
	private TransportStatus transportStatus;
	private Date eta;
	private HandlingActivity nextExpectedHandlingActivity;
	private UnLocode lastKnownLocation;
	private VoyageNumber currentVoyage;
	private boolean isUnloadedAtDestination;

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

	static Delivery derivedFrom(RouteSpecification routeSpecification,
			Itinerary itinerary, HandlingEvent handlingEvent) {
		return new Delivery(itinerary, routeSpecification, handlingEvent);
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

	private Delivery(Itinerary itinerary,
			RouteSpecification routeSpecification, HandlingEvent handlingEvent) {
		Validate.notNull(routeSpecification, "Route specification is required");
		this.routingStatus = calculateRoutingStatus(itinerary,
				routeSpecification);
		this.transportStatus = calculateTransportStatus(handlingEvent);
		this.eta = calculateEta(itinerary);
		this.nextExpectedHandlingActivity = calculateNextExpectedActivity(
				routeSpecification, itinerary, handlingEvent);
		this.lastKnownLocation = calculateLastKnownLocation(handlingEvent);

		this.currentVoyage = calculateCurrentVoyage(handlingEvent);

		this.isUnloadedAtDestination = calculateUnloadedAtDestination(
				routeSpecification, handlingEvent);
	}

	public Delivery(Itinerary itinerary, RouteSpecification routeSpecification) {
		this(itinerary, routeSpecification, null);
	}

	private HandlingActivity calculateNextExpectedActivity(
			RouteSpecification routeSpecification, Itinerary itinerary,
			HandlingEvent lastHandlingEvent) {

		if (lastHandlingEvent == null) {
			return new HandlingActivity(HandlingType.RECEIVE,
					routeSpecification.getOrigin());
		}

		switch (lastHandlingEvent.type()) {
		case RECEIVE:
			final Leg firstLeg = itinerary.getLegs().iterator().next();
			return new HandlingActivity(HandlingType.LOAD,
					firstLeg.getLoadLocation(), firstLeg.getVoyageNumber());

		case LOAD:
			for (Leg leg : itinerary.getLegs()) {
				if (leg.getLoadLocation().equals(lastHandlingEvent.location())) {
					return new HandlingActivity(HandlingType.UNLOAD,
							leg.getUnloadLocation(), leg.getVoyageNumber());
				}
			}

		case UNLOAD:
			for (Iterator<Leg> it = itinerary.getLegs().iterator(); it
					.hasNext();) {
				final Leg leg = it.next();
				if (leg.getUnloadLocation()
						.equals(lastHandlingEvent.location())) {
					if (it.hasNext()) {
						final Leg nextLeg = it.next();
						return new HandlingActivity(HandlingType.LOAD,
								nextLeg.getLoadLocation(),
								nextLeg.getVoyageNumber());
					} else {
						return new HandlingActivity(HandlingType.CLAIM,
								leg.getUnloadLocation());
					}
				}
			}
		default:
			return HandlingActivity.NO_ACTIVITY;
		}

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

	private TransportStatus calculateTransportStatus(
			HandlingEvent lastHandlingEvent) {
		if (lastHandlingEvent == null) {
			return TransportStatus.NOT_RECEIVED;
		}

		switch (lastHandlingEvent.type()) {
		case RECEIVE:
			return TransportStatus.IN_PORT;
		case LOAD:
			return TransportStatus.ONBOARD_CARRIER;
		case UNLOAD:
			return TransportStatus.IN_PORT;
		case CLAIM:
			return TransportStatus.CLAIMED;
		default:
			return TransportStatus.UNKNOWN;
		}
	}

	private VoyageNumber calculateCurrentVoyage(HandlingEvent lastHandlingEvent) {
		if (transportStatus().equals(TransportStatus.ONBOARD_CARRIER)
				&& lastHandlingEvent != null) {
			return lastHandlingEvent.voyage();
		} else {
			return VoyageNumber.none();
		}
	}

	private UnLocode calculateLastKnownLocation(HandlingEvent lastHandlingEvent) {
		if (lastHandlingEvent != null) {
			return lastHandlingEvent.location();
		} else {
			return null;
		}
	}

	private boolean calculateUnloadedAtDestination(
			RouteSpecification routeSpecification,
			HandlingEvent lastHandlingEvent) {
		return lastHandlingEvent != null
				&& HandlingType.UNLOAD.equals(lastHandlingEvent.type())
				&& routeSpecification.getDestination().equals(
						lastHandlingEvent.location());
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

	public TransportStatus transportStatus() {
		return transportStatus;
	}

	public Date eta() {
		return eta;
	}

	public UnLocode lastKnownLocation() {
		return lastKnownLocation;
	}

	public VoyageNumber currentVoyage() {
		return currentVoyage;
	}

	public boolean isUnloadedAtDestination() {
		return isUnloadedAtDestination;
	}
}
