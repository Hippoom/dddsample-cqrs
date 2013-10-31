package com.github.hippoom.dddsample.cargocqrs.persistence.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.github.hippoom.dddsample.cargocqrs.core.AggregateIdentifierGenerator;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;

public class JpaTrackingIdGenerator implements
		AggregateIdentifierGenerator<TrackingId> {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public TrackingId nextIdentifier() {
		return TrackingId.of((Number) entityManager.createNativeQuery(
				"select seq_cargo_tracking_id.nextval from dual")
				.getSingleResult());
	}

}
