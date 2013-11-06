package com.github.hippoom.dddsample.cargocqrs.event;

import java.util.Date;

import lombok.Getter;

import com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus;

@Getter
public class CargoRegisteredEvent {
	private final String trackingId;
	private final String originUnlocode;
	private final String destinationUnlocode;
	private final Date arrivalDeadline;
	private final String routingStatus;

	public CargoRegisteredEvent(String trackingId, String originUnlocode,
			String destinationUnlocode, Date arrivalDeadline,
			RoutingStatus routingStatus) {
		this.trackingId = trackingId;
		this.originUnlocode = originUnlocode;
		this.destinationUnlocode = destinationUnlocode;
		this.arrivalDeadline = arrivalDeadline;
		this.routingStatus = routingStatus.getCode();
	}

}
