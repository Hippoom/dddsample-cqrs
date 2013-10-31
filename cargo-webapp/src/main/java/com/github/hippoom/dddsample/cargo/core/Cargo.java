package com.github.hippoom.dddsample.cargo.core;

import java.util.Date;

import lombok.Getter;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import com.github.hippoom.dddsample.cargo.event.CargoRegisteredEvent;

@SuppressWarnings("serial")
public class Cargo extends AbstractAnnotatedAggregateRoot<TrackingId> {
	@AggregateIdentifier
	@Getter
	private TrackingId trackingId;

	public Cargo(TrackingId trackingId, UnLocode originUnLocode,
			UnLocode destinationUnLocode, Date arrivalDeadline) {

		apply(new CargoRegisteredEvent(trackingId.getValue(),
				originUnLocode.getUnlocode(),
				destinationUnLocode.getUnlocode(), arrivalDeadline));
	}

	@EventHandler
	private void on(CargoRegisteredEvent event) {
		this.trackingId = TrackingId.of(event.getTrackingId());
	}

	/**
	 * frameworks only
	 */
	private Cargo() {

	}
}
