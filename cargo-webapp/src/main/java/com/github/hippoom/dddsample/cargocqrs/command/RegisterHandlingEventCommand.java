package com.github.hippoom.dddsample.cargocqrs.command;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.github.hippoom.dddsample.cargocqrs.core.HandlingActivity;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingEvent;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber;

@EqualsAndHashCode
@ToString
@Getter
public class RegisterHandlingEventCommand {
	private final Date completionTime;
	private final TrackingId trackingId;
	private final UnLocode location;
	private final VoyageNumber voyageNumber;
	private final HandlingType type;
	private final Date registrationTime;

	public RegisterHandlingEventCommand(Date completionTime,
			TrackingId trackingId, UnLocode location, HandlingType type,
			VoyageNumber voyageNumber, Date registrationTime) {
		this.completionTime = completionTime;
		this.trackingId = trackingId;
		this.location = location;
		this.type = type;
		this.voyageNumber = voyageNumber;
		this.registrationTime = registrationTime;
	}

	public HandlingEvent handlingEvent() {
		return new HandlingEvent(new HandlingActivity(type, location,
				voyageNumber), completionTime, registrationTime);
	}
}
