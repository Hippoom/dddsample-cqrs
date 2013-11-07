package com.github.hippoom.dddsample.cargocqrs.event;

import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber;

import lombok.Getter;

@Getter
public class CargoCurrentVoyageUpdatedEvent {
	private final String trackingId;
	private final String voyageNumber;

	public CargoCurrentVoyageUpdatedEvent(String trackingId, String voyageNumber) {
		this.trackingId = trackingId;
		this.voyageNumber = voyageNumber;
	}

	public CargoCurrentVoyageUpdatedEvent(TrackingId trackingId,
			VoyageNumber currentVoyage) {
		this(trackingId.getValue(), currentVoyage == null ? null
				: currentVoyage.getNumber());
	}
}
