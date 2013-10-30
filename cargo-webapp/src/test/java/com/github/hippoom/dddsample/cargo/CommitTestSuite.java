package com.github.hippoom.dddsample.cargo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.hippoom.dddsample.cargo.application.BookingServiceUnitTests;
import com.github.hippoom.dddsample.cargo.application.CargoHandlerUnitTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ BookingServiceUnitTests.class,
		CargoHandlerUnitTests.class })
public class CommitTestSuite {

}
