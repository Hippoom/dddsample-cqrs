package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.Date;

import lombok.Getter;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import com.github.hippoom.dddsample.cargocqrs.event.CargoAssignedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoEtaCalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.event.NextExpectedHandlingActivityCalculatedEvent;

@SuppressWarnings("serial")
public class Cargo extends AbstractAnnotatedAggregateRoot<TrackingId> {
	@AggregateIdentifier
	@Getter
	private TrackingId trackingId;

	private RouteSpecification routeSpecification;

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

	@EventHandler
	private void on(CargoRegisteredEvent event) {
		this.trackingId = TrackingId.of(event.getTrackingId());
		this.routeSpecification = new RouteSpecification(new UnLocode(
				event.getOriginUnlocode()), new UnLocode(
				event.getDestinationUnlocode()), event.getArrivalDeadline());
	}

	/**
	 * frameworks only
	 */
	private Cargo() {

	}
}
