package com.github.hippoom.dddsample.cargo.core;

public interface CargoRepository {

	TrackingId nextTrackingId();

	void store(Cargo cargo);

}
