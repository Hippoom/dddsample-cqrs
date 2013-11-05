package com.github.hippoom.dddsample.cargocqrs.application;

import lombok.Setter;

import org.axonframework.commandhandling.annotation.CommandHandler;

import com.github.hippoom.dddsample.cargocqrs.command.AssignCargoToRouteCommand;
import com.github.hippoom.dddsample.cargocqrs.command.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargocqrs.core.Cargo;
import com.github.hippoom.dddsample.cargocqrs.core.CargoRepository;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;

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

	@CommandHandler
	public void handle(AssignCargoToRouteCommand command) {
		final Cargo cargo = cargoRepository.findBy(command.getTrackingId());
		cargo.assignToRoute(command.getItinerary());
	}
}
