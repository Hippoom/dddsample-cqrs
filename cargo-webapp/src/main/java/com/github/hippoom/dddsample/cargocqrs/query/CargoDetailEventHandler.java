package com.github.hippoom.dddsample.cargocqrs.query;

import lombok.Setter;

import org.axonframework.eventhandling.annotation.EventHandler;

import com.github.hippoom.dddsample.cargocqrs.core.TransportStatus;
import com.github.hippoom.dddsample.cargocqrs.event.CargoAssignedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoCurrentVoyageUpdatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoEtaCalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoIsClaimedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoIsUnloadedAtDestinationEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoLastKnownLocationUpdatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoTransportStatusRecalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.NextExpectedHandlingActivityCalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.persistence.CargoDetailDao;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

public class CargoDetailEventHandler {
	@Setter
	private CargoDetailDao cargoDetailDao;

	@EventHandler
	public void on(CargoRegisteredEvent event) {
		final CargoDto cargo = from(event);
		cargo.setTransportStatus(TransportStatus.NOT_RECEIVED.getCode());
		cargoDetailDao.store(cargo);
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

	@EventHandler
	public void on(NextExpectedHandlingActivityCalculatedEvent event) {
		final CargoDto cargo = cargoDetailDao.findBy(event.getTrackingId());
		cargo.setNextExpectedHandlingActivityType(event.getType());
		cargo.setNextExpectedHandlingActivityLocation(event.getLocation());
		cargo.setNextExpectedHandlingActivityVoyageNumber(event
				.getVoyageNumber());
		cargoDetailDao.store(cargo);
	}

	@EventHandler
	public void on(CargoTransportStatusRecalculatedEvent event) {
		final CargoDto cargo = cargoDetailDao.findBy(event.getTrackingId());
		cargo.setTransportStatus(event.getTransportStatus());
		cargoDetailDao.store(cargo);
	}

	@EventHandler
	public void on(CargoLastKnownLocationUpdatedEvent event) {
		final CargoDto cargo = cargoDetailDao.findBy(event.getTrackingId());
		cargo.setLastKnownLocation(event.getLocation());
		cargoDetailDao.store(cargo);
	}

	@EventHandler
	public void on(CargoCurrentVoyageUpdatedEvent event) {
		final CargoDto cargo = cargoDetailDao.findBy(event.getTrackingId());
		cargo.setCurrentVoyageNumber(event.getVoyageNumber());
		cargoDetailDao.store(cargo);
	}

	@EventHandler
	public void on(CargoIsUnloadedAtDestinationEvent event) {
		final CargoDto cargo = cargoDetailDao.findBy(event.getTrackingId());
		cargo.setUnloadedAtDestinationIndicator("1");
		cargoDetailDao.store(cargo);
	}

	@EventHandler
	public void on(CargoIsClaimedEvent event) {
		final CargoDto cargo = cargoDetailDao.findBy(event.getTrackingId());
		cargo.setNextExpectedHandlingActivityType(null);
		cargo.setNextExpectedHandlingActivityLocation(null);
		cargo.setNextExpectedHandlingActivityVoyageNumber(null);
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
