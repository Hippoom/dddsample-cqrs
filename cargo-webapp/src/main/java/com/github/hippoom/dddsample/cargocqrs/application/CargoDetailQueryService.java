package com.github.hippoom.dddsample.cargocqrs.application;

import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

public interface CargoDetailQueryService {

	CargoDto findBy(String trackingId);

}