package com.github.hippoom.dddsample.cargocqrs.event;

import java.util.Date;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.HandlingEvent;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;

@Getter
public class HandlingEventRegisteredEvent {
	private final Date completionTime;
	private final TrackingId trackingId;
	private final UnLocode location;
	private final HandlingType type;
	private final Date registrationTime;

	public HandlingEventRegisteredEvent(Date completionTime,
			TrackingId trackingId, UnLocode location, HandlingType type,
			Date registrationTime) {
		this.completionTime = completionTime;
		this.trackingId = trackingId;
		this.location = location;
		this.type = type;
		this.registrationTime = registrationTime;
	}

	public HandlingEventRegisteredEvent(TrackingId trackingId,
			HandlingEvent handlingEvent) {
		this(handlingEvent.completionTime(), trackingId, handlingEvent
				.location(), handlingEvent.type(), handlingEvent
				.registrationTime());
	}
}
