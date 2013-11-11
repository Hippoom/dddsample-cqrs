package com.github.hippoom.dddsample.cargocqrs.event;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;

@Getter
public class CargoIsClaimedEvent {
	private final String trackingId;

	public CargoIsClaimedEvent(String trackingId) {
		this.trackingId = trackingId;
	}

	public CargoIsClaimedEvent(TrackingId trackingId) {
		this(trackingId.getValue());
	}
}
