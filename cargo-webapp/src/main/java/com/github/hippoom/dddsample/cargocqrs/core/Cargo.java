package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.Date;

import lombok.Getter;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import com.github.hippoom.dddsample.cargocqrs.event.CargoAssignedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoCurrentVoyageUpdatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoEtaCalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoIsClaimedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoIsUnloadedAtDestinationEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoLastKnownLocationUpdatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoTransportStatusRecalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.HandlingEventRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.event.NextExpectedHandlingActivityCalculatedEvent;

@SuppressWarnings("serial")
public class Cargo extends AbstractAnnotatedAggregateRoot<TrackingId> {
	@AggregateIdentifier
	@Getter
	private TrackingId trackingId;

	private RouteSpecification routeSpecification;

	private Itinerary itinerary;

	public Cargo(TrackingId trackingId, UnLocode originUnLocode,
			UnLocode destinationUnLocode, Date arrivalDeadline) {
		Delivery delivery = Delivery.derivedFrom(new RouteSpecification(
				originUnLocode, destinationUnLocode, arrivalDeadline));
		apply(new CargoRegisteredEvent(trackingId.getValue(),
				originUnLocode.getUnlocode(),
				destinationUnLocode.getUnlocode(), arrivalDeadline,
				delivery.routingStatus()));
	}

	/**
	 * Attach a new itinerary to this cargo.
	 * 
	 * @param itinerary
	 *            an itinerary. May not be null.
	 */
	public void assignToRoute(final Itinerary itinerary) {
		if (routeSpecification.isSatisfiedBy(itinerary)) {
			Delivery delivery = Delivery.derivedFrom(routeSpecification,
					itinerary);
			apply(new CargoAssignedEvent(this.trackingId, itinerary,
					delivery.routingStatus()));
			apply(new CargoEtaCalculatedEvent(this.trackingId, delivery.eta()));
			apply(new NextExpectedHandlingActivityCalculatedEvent(
					this.trackingId, delivery.nextExpectedHandlingActivity()));
		} else {
			throw new CannotAssignCargoToRouteException(this.trackingId,
					this.routeSpecification, itinerary);
		}
	}

	/**
	 * Updates all aspects of the cargo aggregate status based on the current
	 * route specification, itinerary and handling of the cargo.
	 * <p/>
	 * When either of those three changes, i.e. when a new route is specified
	 * for the cargo, the cargo is assigned to a route or when the cargo is
	 * handled, the status must be re-calculated.
	 * <p/>
	 * {@link RouteSpecification} and {@link Itinerary} are both inside the
	 * Cargo aggregate, so changes to them cause the status to be updated
	 * <b>synchronously</b>, but changes to the delivery history (when a cargo
	 * is handled) cause the status update to happen <b>asynchronously</b> since
	 * {@link HandlingEvent} is in a different aggregate.
	 * 
	 * @param handlingHistory
	 *            handling history
	 */
	public void deriveDeliveryProgress(final HandlingEvent handlingEvent) {
		Delivery delivery = Delivery.derivedFrom(routeSpecification, itinerary,
				handlingEvent);
		apply(new HandlingEventRegisteredEvent(this.trackingId, handlingEvent));
		apply(new CargoTransportStatusRecalculatedEvent(this.trackingId,
				delivery.transportStatus()));
		apply(new CargoLastKnownLocationUpdatedEvent(this.trackingId,
				delivery.lastKnownLocation()));
		if (delivery.currentVoyage() != null) {
			apply(new CargoCurrentVoyageUpdatedEvent(this.trackingId,
					delivery.currentVoyage()));
		}
		if (delivery.nextExpectedHandlingActivity() != null) {
			apply(new NextExpectedHandlingActivityCalculatedEvent(
					this.trackingId, delivery.nextExpectedHandlingActivity()));
		} else {
			apply(new CargoIsClaimedEvent(this.trackingId));
		}
		if (delivery.isUnloadedAtDestination()) {
			apply(new CargoIsUnloadedAtDestinationEvent(this.trackingId));
		}
	}

	@EventHandler
	private void on(CargoRegisteredEvent event) {
		this.trackingId = TrackingId.of(event.getTrackingId());
		this.routeSpecification = new RouteSpecification(new UnLocode(
				event.getOriginUnlocode()), new UnLocode(
				event.getDestinationUnlocode()), event.getArrivalDeadline());
	}

	@EventHandler
	private void on(CargoAssignedEvent event) {
		this.itinerary = event.itinerary();
	}

	/**
	 * frameworks only
	 */
	private Cargo() {

	}
}
