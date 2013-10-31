package com.github.hippoom.dddsample.cargo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.hippoom.dddsample.cargo.application.BookingServiceUnitTests;
import com.github.hippoom.dddsample.cargo.application.CargoCommandHandlerUnitTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ BookingServiceUnitTests.class,
		CargoCommandHandlerUnitTests.class })
public class CommitTestSuite {

}
