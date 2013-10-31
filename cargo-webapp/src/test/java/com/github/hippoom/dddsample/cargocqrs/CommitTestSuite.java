package com.github.hippoom.dddsample.cargocqrs;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.hippoom.dddsample.cargocqrs.application.BookingServiceUnitTests;
import com.github.hippoom.dddsample.cargocqrs.application.CargoCommandHandlerUnitTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ BookingServiceUnitTests.class,
		CargoCommandHandlerUnitTests.class })
public class CommitTestSuite {

}
