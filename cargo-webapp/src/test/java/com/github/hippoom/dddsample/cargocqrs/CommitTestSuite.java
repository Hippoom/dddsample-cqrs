package com.github.hippoom.dddsample.cargocqrs;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.hippoom.dddsample.cargocqrs.application.BookingServiceUnitTests;
import com.github.hippoom.dddsample.cargocqrs.application.CargoCommandHandlerUnitTests;
import com.github.hippoom.dddsample.cargocqrs.pathfinder.ItineraryTranslatorIntegrationTests;
import com.github.hippoom.dddsample.cargocqrs.pathfinder.PathFinderRestClientIntegrationTests;
import com.github.hippoom.dddsample.cargocqrs.pathfinder.RoutingServiceIntegrationTests;
import com.github.hippoom.dddsample.cargocqrs.persistence.jpa.JpaCargoDetailQueryServicePersistenceTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ BookingServiceUnitTests.class,
		CargoCommandHandlerUnitTests.class,
		JpaCargoDetailQueryServicePersistenceTests.class,
		RoutingServiceIntegrationTests.class,
		ItineraryTranslatorIntegrationTests.class,
		PathFinderRestClientIntegrationTests.class })
public class CommitTestSuite {

}
