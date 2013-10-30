package com.github.hippoom.dddsample.cargo.application;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.hippoom.dddsample.cargo.command.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargo.core.Cargo;
import com.github.hippoom.dddsample.cargo.core.CargoRepository;
import com.github.hippoom.dddsample.cargo.core.TrackingId;
import com.github.hippoom.dddsample.cargo.core.UnLocode;
import com.github.hippoom.dddsample.cargo.event.CargoRegisteredEvent;

public class CargoHandlerUnitTests {

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private CargoHandler target = new CargoHandler();

	private FixtureConfiguration<Cargo> fixture = Fixtures
			.newGivenWhenThenFixture(Cargo.class);

	@Mock
	private CargoRepository cargoRepository;

	@Before
	public void injects() throws Throwable {
		target.setCargoRepository(cargoRepository);

		fixture.registerAnnotatedCommandHandler(target);
	}

	@Test
	public void storesNewCargo() throws Throwable {
		final UnLocode originUnLocode = new UnLocode("SHA");
		final UnLocode destinationUnLocode = new UnLocode("PEK");
		final Date arrivalDeadline = new Date();

		final TrackingId trackingId = new TrackingId("1");

		context.checking(new Expectations() {
			{
				allowing(cargoRepository).nextTrackingId();
				will(returnValue(trackingId));

				oneOf(cargoRepository).store(with(any(Cargo.class)));// TODO
																		// refactor
																		// this
																		// with
																		// a
																		// factory
			}
		});

		fixture.given()
				.when(new RegisterCargoCommand(originUnLocode,
						destinationUnLocode, arrivalDeadline))
				.expectEvents(
						new CargoRegisteredEvent(trackingId.getValue(),
								originUnLocode.getUnlocode(),
								destinationUnLocode.getUnlocode(),
								arrivalDeadline));

		//assertThat(actual, equalTo(trackingId));

	}
}
