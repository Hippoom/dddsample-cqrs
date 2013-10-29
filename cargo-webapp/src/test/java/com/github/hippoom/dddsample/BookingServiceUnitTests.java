package com.github.hippoom.dddsample;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.hippoom.dddsample.cargo.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargo.axon.GenericCommandGateway;
import com.github.hippoom.dddsample.cargo.core.TrackingId;
import com.github.hippoom.dddsample.cargo.core.UnLocode;

public class BookingServiceUnitTests {

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private BookingService target = new BookingService();

	@Mock
	private GenericCommandGateway commandGateway;

	@Before
	public void injects() throws Throwable {
		target.setCommandGateway(commandGateway);
	}

	@Test
	public void returnsTrackingIdWhenANewCargoIsRegistered() throws Throwable {
		final UnLocode originUnLocode = new UnLocode("SHA");
		final UnLocode destinationUnLocode = new UnLocode("PEK");
		final Date arrivalDeadline = new Date();

		final TrackingId trackingId = new TrackingId("1");

		context.checking(new Expectations() {
			{
				oneOf(commandGateway).sendAndWait(
						new RegisterCargoCommand(originUnLocode,
								destinationUnLocode, arrivalDeadline));
				will(returnValue(trackingId));
			}
		});

		assertThat(target.register(originUnLocode, destinationUnLocode,
				arrivalDeadline), equalTo(trackingId));
	}
}
