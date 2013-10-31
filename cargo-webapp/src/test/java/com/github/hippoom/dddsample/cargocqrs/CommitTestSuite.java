package com.github.hippoom.dddsample.cargocqrs;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.hippoom.dddsample.cargocqrs.application.BookingServiceUnitTests;
import com.github.hippoom.dddsample.cargocqrs.application.CargoCommandHandlerUnitTests;
import com.github.hippoom.dddsample.cargocqrs.application.CargoDetailQueryServicePersistenceTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ BookingServiceUnitTests.class,
		CargoCommandHandlerUnitTests.class,
		CargoDetailQueryServicePersistenceTests.class })
public class CommitTestSuite {

}
