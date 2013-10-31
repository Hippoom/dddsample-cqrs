package com.github.hippoom.dddsample.cargocqrs.application;

import java.util.Date;

import lombok.Setter;

import com.github.hippoom.dddsample.cargocqrs.axon.GenericCommandGateway;
import com.github.hippoom.dddsample.cargocqrs.command.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;

public class BookingService {
	@Setter
	private GenericCommandGateway commandGateway;

	public TrackingId register(UnLocode originUnLocode,
			UnLocode destinationUnLocode, Date arrivalDeadline) {
		return commandGateway.sendAndWait(new RegisterCargoCommand(
				originUnLocode, destinationUnLocode, arrivalDeadline));
	}

}
