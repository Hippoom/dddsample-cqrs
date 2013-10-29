package com.github.hippoom.dddsample;

import java.util.Date;

import lombok.Setter;

import com.github.hippoom.dddsample.cargo.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargo.axon.GenericCommandGateway;
import com.github.hippoom.dddsample.cargo.core.TrackingId;
import com.github.hippoom.dddsample.cargo.core.UnLocode;

public class BookingService {
	@Setter
	private GenericCommandGateway commandGateway;

	public TrackingId register(UnLocode originUnLocode,
			UnLocode destinationUnLocode, Date arrivalDeadline) {
		return commandGateway.sendAndWait(new RegisterCargoCommand(
				originUnLocode, destinationUnLocode, arrivalDeadline));
	}

}
