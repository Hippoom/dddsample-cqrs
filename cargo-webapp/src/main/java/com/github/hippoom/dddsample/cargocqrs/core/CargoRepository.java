package com.github.hippoom.dddsample.cargocqrs.core;

import lombok.Setter;

import org.axonframework.repository.Repository;

public class CargoRepository {
	@Setter
	private Repository<Cargo> delegate;
	@Setter
	private AggregateIdentifierGenerator<TrackingId> identifierGenerator;

	public TrackingId nextTrackingId() {
		return identifierGenerator.nextIdentifier();
	}

	public void store(Cargo cargo) {
		delegate.add(cargo);
	}

	public Cargo findBy(TrackingId trackingId) {
		return delegate.load(trackingId);
	}

}
