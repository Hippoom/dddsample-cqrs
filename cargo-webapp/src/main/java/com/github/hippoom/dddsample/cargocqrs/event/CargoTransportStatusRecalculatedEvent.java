package com.github.hippoom.dddsample.cargocqrs.event;

import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.TransportStatus;

import lombok.Getter;

@Getter
public class CargoTransportStatusRecalculatedEvent {
	private final String trackingId;
	private final String transportStatus;

	public CargoTransportStatusRecalculatedEvent(String trackingId,
			String transportStatus) {
		this.trackingId = trackingId;
		this.transportStatus = transportStatus;
	}

	public CargoTransportStatusRecalculatedEvent(TrackingId trackingId,
			TransportStatus transportStatus) {
		this(trackingId.getValue(), transportStatus.getCode());
	}
}
