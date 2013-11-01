package com.github.hippoom.dddsample.cargocqrs.persistence;

import com.github.hippoom.dddsample.cargocqrs.application.CargoDetailQueryService;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

public interface CargoDetailDao extends CargoDetailQueryService {

	void save(CargoDto cargo);

}