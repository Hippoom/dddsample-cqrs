package com.github.hippoom.dddsample.cargocqrs.application;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.hippoom.dddsample.cargocqrs.axon.GenericCommandGateway;
import com.github.hippoom.dddsample.cargocqrs.command.RegisterHandlingEventCommand;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber;
import com.github.hippoom.dddsample.cargocqrs.time.Clock;

public class HandlingApplicationUnitTests {

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private HandlingApplication target = new HandlingApplication();

	@Mock
	private GenericCommandGateway commandGateway;

	@Mock
	private Clock clock;

	@Before
	public void injects() throws Throwable {
		target.setCommandGateway(commandGateway);
		target.setClock(clock);
	}

	@Test
	public void returnsTrackingIdWhenANewCargoIsRegistered() throws Throwable {

		final Date completionTime = new Date();
		final TrackingId trackingId = new TrackingId("1");
		final UnLocode location = new UnLocode("CNSHA");
		final HandlingType type = HandlingType.RECEIVE;
		final Date registrationTime = new Date();
		final VoyageNumber voyageNumber = new VoyageNumber("CM001");

		context.checking(new Expectations() {
			{
				allowing(clock).now();
				will(returnValue(registrationTime));

				oneOf(commandGateway).sendAndWait(
						new RegisterHandlingEventCommand(completionTime,
								trackingId, location, type, voyageNumber,
								registrationTime));
			}
		});

		target.registerHandlingEvent(completionTime, trackingId, location,
				type, voyageNumber);
	}
}
