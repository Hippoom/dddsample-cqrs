package com.github.hippoom.dddsample.cargocqrs.event;

import java.util.Date;

import lombok.Getter;

@Getter
public class CargoRegisteredEvent {
	private final String trackingId;
	private final String originUnlocode;
	private final String destinationUnlocode;
	private final Date arrivalDeadline;

	public CargoRegisteredEvent(String trackingId, String originUnlocode,
			String destinationUnlocode, Date arrivalDeadline) {
		this.trackingId = trackingId;
		this.originUnlocode = originUnlocode;
		this.destinationUnlocode = destinationUnlocode;
		this.arrivalDeadline = arrivalDeadline;
	}

}
