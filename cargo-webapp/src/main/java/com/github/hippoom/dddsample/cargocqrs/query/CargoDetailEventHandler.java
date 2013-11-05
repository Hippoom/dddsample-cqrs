package com.github.hippoom.dddsample.cargocqrs.query;

import lombok.Setter;

import org.axonframework.eventhandling.annotation.EventHandler;

import com.github.hippoom.dddsample.cargocqrs.event.CargoAssignedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoEtaCalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.persistence.CargoDetailDao;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

public class CargoDetailEventHandler {
	@Setter
	private CargoDetailDao cargoDetailDao;

	@EventHandler
	public void on(CargoRegisteredEvent event) {
		cargoDetailDao.store(from(event));
	}

	@EventHandler
	public void on(CargoAssignedEvent event) {
		final CargoDto cargo = cargoDetailDao.findBy(event.getTrackingId());
		cargo.setRoutingStatus(event.getRoutingStatus());
		cargo.setLegs(event.getRoute().getLegs());
		cargoDetailDao.store(cargo);
	}

	@EventHandler
	public void on(CargoEtaCalculatedEvent event) {
		final CargoDto cargo = cargoDetailDao.findBy(event.getTrackingId());
		cargo.setEta(event.getEta());
		cargoDetailDao.store(cargo);
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
