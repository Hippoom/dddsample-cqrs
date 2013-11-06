package com.github.hippoom.dddsample.cargocqrs.application;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.hippoom.dddsample.cargocqrs.axon.GenericCommandGateway;
import com.github.hippoom.dddsample.cargocqrs.command.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.RouteSpecification;
import com.github.hippoom.dddsample.cargocqrs.core.RoutingService;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

public class BookingServiceUnitTests {

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private BookingService target = new BookingService();

	@Mock
	private GenericCommandGateway commandGateway;

	@Mock
	private CargoDetailQueryService cargoDetailQueryService;

	@Mock
	private RoutingService routingService;

	@Before
	public void injects() throws Throwable {
		target.setCommandGateway(commandGateway);
		target.setCargoDetailQueryService(cargoDetailQueryService);
		target.setRoutingService(routingService);
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

	@Test
	public void returnsItineriesMachtedForCargo() throws Throwable {
		final TrackingId trackingId = TrackingId.of("1");
		final CargoDto cargo = new CargoDto();
		cargo.setTrackingId(trackingId.getValue());
		cargo.setOriginUnlocode("CNSHA");
		cargo.setDestinationUnlocode("CNPEK");
		cargo.setArrivalDeadline(DateTime.now().plusDays(7).toDate());

		final RouteSpecification routeSpec = new RouteSpecification(
				new UnLocode(cargo.getOriginUnlocode()), new UnLocode(
						cargo.getDestinationUnlocode()),
				cargo.getArrivalDeadline());

		final List<Itinerary> expect = new ArrayList<Itinerary>();

		context.checking(new Expectations() {
			{
				allowing(cargoDetailQueryService).findBy(trackingId.getValue());
				will(returnValue(cargo));

				allowing(routingService).fetchRoutesForSpecification(routeSpec);
				will(returnValue(expect));
			}
		});

		List<Itinerary> actual = target
				.requestPossibleRoutesForCargo(trackingId);

		assertThat(actual, sameInstance(expect));

	}
}
