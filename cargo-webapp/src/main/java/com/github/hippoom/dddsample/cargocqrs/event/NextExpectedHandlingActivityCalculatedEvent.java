package com.github.hippoom.dddsample.cargocqrs.event;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.HandlingActivity;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber;

@Getter
public class NextExpectedHandlingActivityCalculatedEvent {
	private final String trackingId;
	private final String type;
	private final String voyageNumber;
	private final String location;

	public NextExpectedHandlingActivityCalculatedEvent(TrackingId trackingId,
			HandlingType type, VoyageNumber voyageNumber, UnLocode location) {
		this.trackingId = trackingId.getValue();
		this.type = type.getCode();
		this.voyageNumber = voyageNumber == null ? null : voyageNumber
				.getNumber();
		this.location = location.getUnlocode();
	}

	public NextExpectedHandlingActivityCalculatedEvent(TrackingId trackingId,
			HandlingActivity nextExpectedHandlingActivity) {
		this(trackingId, nextExpectedHandlingActivity.type(),
				nextExpectedHandlingActivity.voyage(),
				nextExpectedHandlingActivity.location());
	}
}
