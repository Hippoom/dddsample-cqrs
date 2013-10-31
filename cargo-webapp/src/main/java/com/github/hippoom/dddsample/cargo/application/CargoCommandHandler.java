package com.github.hippoom.dddsample.cargo.application;

import lombok.Setter;

import org.axonframework.commandhandling.annotation.CommandHandler;

import com.github.hippoom.dddsample.cargo.command.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargo.core.Cargo;
import com.github.hippoom.dddsample.cargo.core.CargoRepository;
import com.github.hippoom.dddsample.cargo.core.TrackingId;

public class CargoCommandHandler {
	@Setter
	private CargoRepository cargoRepository;
	
	@CommandHandler
	public TrackingId handle(RegisterCargoCommand command) {
		final Cargo cargo = new Cargo(cargoRepository.nextTrackingId(),
				command.getOriginUnLocode(), command.getDestinationUnLocode(),
				command.getArrivalDeadline());
		cargoRepository.store(cargo);
		return cargo.getTrackingId();
	}

}
