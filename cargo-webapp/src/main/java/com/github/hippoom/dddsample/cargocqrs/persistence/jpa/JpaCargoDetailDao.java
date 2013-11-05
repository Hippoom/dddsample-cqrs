package com.github.hippoom.dddsample.cargocqrs.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.github.hippoom.dddsample.cargocqrs.persistence.CargoDetailDao;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

@Transactional(readOnly = true)
public class JpaCargoDetailDao implements CargoDetailDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public CargoDto findBy(String trackingId) {
		return entityManager.find(CargoDto.class, trackingId);
	}

	@Override
	public CargoDto findWithLegsBy(String trackingId) {
		CargoDto cargo = findBy(trackingId);
		cargo.getLegs().size();
		return cargo;
	}

	@Override
	@Transactional
	public void store(CargoDto cargo) {
		entityManager.persist(cargo);
	}

}
