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
	@Transactional
	public void save(CargoDto cargo) {
		entityManager.persist(cargo);
	}

}
