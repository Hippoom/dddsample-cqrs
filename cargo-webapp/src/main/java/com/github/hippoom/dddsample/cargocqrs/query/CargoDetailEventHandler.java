package com.github.hippoom.dddsample.cargocqrs.query;

import lombok.Setter;

import org.axonframework.eventhandling.annotation.EventHandler;

import com.github.hippoom.dddsample.cargocqrs.event.CargoRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.persistence.CargoDetailDao;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

public class CargoDetailEventHandler {
	@Setter
	private CargoDetailDao cargoDetailDao;

	@EventHandler
	public void on(CargoRegisteredEvent event) {
		cargoDetailDao.save(from(event));
	}

	private CargoDto from(CargoRegisteredEvent event) {
		final CargoDto cargo = new CargoDto();
		cargo.setTrackingId(event.getTrackingId());
		cargo.setOriginUnlocode(event.getOriginUnlocode());
		cargo.setDestinationUnlocode(event.getDestinationUnlocode());
		cargo.setArrivalDeadline(event.getArrivalDeadline());
		cargo.setRoutingStatus(event.getRoutingStatus());
		return cargo;
	}
}
