package com.github.hippoom.dddsample.cargocqrs.application;

import java.util.Date;

import lombok.Setter;

import com.github.hippoom.dddsample.cargocqrs.axon.GenericCommandGateway;
import com.github.hippoom.dddsample.cargocqrs.command.RegisterHandlingEventCommand;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.time.Clock;

/**
 * Handling event application service.
 */
public class HandlingApplication {
	@Setter
	private GenericCommandGateway commandGateway;
	@Setter
	private Clock clock;

	/**
	 * Registers a handling event in the system, and notifies interested parties
	 * that a cargo has been handled.
	 * 
	 * @param completionTime
	 *            when the event was completed
	 * @param trackingId
	 *            cargo tracking id
	 * @param voyageNumber
	 *            voyage number
	 * @param unLocode
	 *            UN locode for the location where the event occurred
	 * @param type
	 *            type of event
	 */
	public void registerHandlingEvent(Date completionTime,
			TrackingId trackingId, UnLocode location, HandlingType type) {
		commandGateway.sendAndWait(new RegisterHandlingEventCommand(
				completionTime, trackingId, location, type, clock.now()));
	}
}
