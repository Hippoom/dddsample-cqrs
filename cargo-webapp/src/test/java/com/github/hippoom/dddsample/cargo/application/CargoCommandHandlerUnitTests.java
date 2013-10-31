package com.github.hippoom.dddsample.cargo.application;

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
import com.github.hippoom.dddsample.cargo.core.AggregateIdentifierGenerator;
import com.github.hippoom.dddsample.cargo.core.Cargo;
import com.github.hippoom.dddsample.cargo.core.CargoRepository;
import com.github.hippoom.dddsample.cargo.core.TrackingId;
import com.github.hippoom.dddsample.cargo.core.UnLocode;
import com.github.hippoom.dddsample.cargo.event.CargoRegisteredEvent;

public class CargoCommandHandlerUnitTests {

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private CargoCommandHandler target = new CargoCommandHandler();

	private CargoRepository cargoRepository = new CargoRepository();

	private FixtureConfiguration<Cargo> fixture = Fixtures
			.newGivenWhenThenFixture(Cargo.class);

	@Mock
	private AggregateIdentifierGenerator<TrackingId> trackingIdGenerator;

	@Before
	public void injects() throws Throwable {
		fixture.registerAnnotatedCommandHandler(target);

		target.setCargoRepository(cargoRepository);

		cargoRepository.setIdentifierGenerator(trackingIdGenerator);
		cargoRepository.setDelegate(fixture.getRepository());

	}

	@Test
	public void storesNewCargo() throws Throwable {
		final UnLocode originUnLocode = new UnLocode("SHA");
		final UnLocode destinationUnLocode = new UnLocode("PEK");
		final Date arrivalDeadline = new Date();

		final TrackingId trackingId = new TrackingId("1");

		context.checking(new Expectations() {
			{
				allowing(trackingIdGenerator).nextIdentifier();
				will(returnValue(trackingId));

			}
		});

		fixture.given()
				.when(new RegisterCargoCommand(originUnLocode,
						destinationUnLocode, arrivalDeadline))
				.expectEvents(
						new CargoRegisteredEvent(trackingId.getValue(),
								originUnLocode.getUnlocode(),
								destinationUnLocode.getUnlocode(),
								arrivalDeadline)).expectReturnValue(trackingId);

	}
}
