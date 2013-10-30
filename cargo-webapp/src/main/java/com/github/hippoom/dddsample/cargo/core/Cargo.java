package com.github.hippoom.dddsample.cargo.core;

import java.util.Date;

import lombok.Getter;

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

		this.trackingId = trackingId;
		apply(new CargoRegisteredEvent(trackingId.getValue(),
				originUnLocode.getUnlocode(),
				destinationUnLocode.getUnlocode(), arrivalDeadline));
	}

	/**
	 * frameworks only
	 */
	private Cargo() {

	}
}
