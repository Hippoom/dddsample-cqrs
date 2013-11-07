package com.github.hippoom.dddsample.cargocqrs.event;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
@Getter
public class CargoIsUnloadedAtDestinationEvent {
	private final String trackingId;

	public CargoIsUnloadedAtDestinationEvent(String trackingId) {
		this.trackingId = trackingId;
	}

	public CargoIsUnloadedAtDestinationEvent(TrackingId trackingId) {
		this(trackingId.getValue());
	}
}
