package com.github.hippoom.dddsample.cargocqrs.query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.transaction.annotation.Transactional;

import com.github.hippoom.dddsample.cargocqrs.event.CargoRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

@Transactional(readOnly = true)
public class CargoDetailQueryService {

	@PersistenceContext
	private EntityManager entityManager;

	public CargoDto findBy(String trackingId) {
		return entityManager.find(CargoDto.class, trackingId);
	}

	@Transactional
	public void save(CargoDto cargo) {
		entityManager.persist(cargo);
	}

	@EventHandler
	public void on(CargoRegisteredEvent event) {
		save(from(event));
	}

	private CargoDto from(CargoRegisteredEvent event) {
		final CargoDto cargo = new CargoDto();
		cargo.setTrackingId(event.getTrackingId());
		cargo.setOriginUnlocode(event.getOriginUnlocode());
		cargo.setDestinationUnlocode(event.getDestinationUnlocode());
		cargo.setArrivalDeadline(event.getArrivalDeadline());
		return cargo;
	}
}
