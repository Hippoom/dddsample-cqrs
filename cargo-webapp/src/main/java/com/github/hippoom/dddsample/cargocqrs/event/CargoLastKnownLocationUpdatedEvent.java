package com.github.hippoom.dddsample.cargocqrs.event;

import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;

import lombok.Getter;

@Getter
public class CargoLastKnownLocationUpdatedEvent {
	private final String trackingId;
	private final String location;

	public CargoLastKnownLocationUpdatedEvent(String trackingId, String location) {
		this.trackingId = trackingId;
		this.location = location;
	}

	public CargoLastKnownLocationUpdatedEvent(TrackingId trackingId,
			UnLocode location) {
		this.trackingId = trackingId.getValue();
		this.location = location.getUnlocode();
	}
}
