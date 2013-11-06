package com.github.hippoom.dddsample.cargocqrs.event;

import java.util.Date;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;

@Getter
public class CargoEtaCalculatedEvent {

	private final String trackingId;
	private final Date eta;

	public CargoEtaCalculatedEvent(TrackingId trackingId, Date eta) {
		this.trackingId = trackingId.getValue();
		this.eta = eta;
	}

}
