package com.github.hippoom.dddsample.cargocqrs.application;

import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.RECEIVE;
import static com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus.NOT_ROUTED;
import static com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus.ROUTED;
import static com.github.hippoom.dddsample.cargocqrs.core.UnLocodes.CNPEK;
import static com.github.hippoom.dddsample.cargocqrs.core.UnLocodes.CNSHA;
import static com.github.hippoom.dddsample.cargocqrs.core.VoyageNumbers.CM001;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

import java.util.Arrays;
import java.util.Date;

import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.hippoom.dddsample.cargocqrs.command.AssignCargoToRouteCommand;
import com.github.hippoom.dddsample.cargocqrs.command.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargocqrs.command.RegisterHandlingEventCommand;
import com.github.hippoom.dddsample.cargocqrs.core.AggregateIdentifierGenerator;
import com.github.hippoom.dddsample.cargocqrs.core.CannotAssignCargoToRouteException;
import com.github.hippoom.dddsample.cargocqrs.core.Cargo;
import com.github.hippoom.dddsample.cargocqrs.core.CargoRepository;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.Leg;
import com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.TransportStatus;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber;
import com.github.hippoom.dddsample.cargocqrs.event.CargoAssignedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoCurrentVoyageUpdatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoEtaCalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoLastKnownLocationUpdatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.event.CargoTransportStatusRecalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.event.HandlingEventRegisteredEvent;
import com.github.hippoom.dddsample.cargocqrs.event.NextExpectedHandlingActivityCalculatedEvent;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;
import com.github.hippoom.dddsample.cargocqrs.rest.RouteCandidateDto;

public class CargoCommandHandlerUnitTests {

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

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
		final Date arrivalDeadline = new Date();
		final TrackingId trackingId = new TrackingId("1");

		context.checking(new Expectations() {
			{
				allowing(trackingIdGenerator).nextIdentifier();
				will(returnValue(trackingId));

			}
		});

		fixture.given()
				.when(new RegisterCargoCommand(CNSHA, CNPEK, arrivalDeadline))
				.expectEvents(
						new CargoRegisteredEvent(trackingId.getValue(), CNSHA
								.getUnlocode(), CNPEK.getUnlocode(),
								arrivalDeadline, NOT_ROUTED))
				.expectReturnValue(trackingId);

	}

	@Test
	public void assignsCargoToRoute() throws Throwable {
		final TrackingId trackingId = new TrackingId("1");

		final Date loadTime = new DateTime().withDate(2015, 4, 1).toDate();
		final Date unloadTime = new DateTime().withDate(2015, 4, 7).toDate();

		final Itinerary itinerary = new Itinerary(new Leg(CM001, CNSHA, CNPEK,
				loadTime, unloadTime));

		fixture.given(
				new CargoRegisteredEvent(trackingId.getValue(), CNSHA
						.getUnlocode(), CNPEK.getUnlocode(), new DateTime()
						.withDate(2015, 4, 8).toDate(), NOT_ROUTED))
				.when(new AssignCargoToRouteCommand(trackingId, itinerary))
				.expectEvents(
						new CargoAssignedEvent(trackingId.getValue(),
								new RouteCandidateDto(Arrays.asList(new LegDto(
										0, "CM001", "CNSHA", "CNPEK", loadTime,
										unloadTime))), ROUTED.getCode()),
						new CargoEtaCalculatedEvent(trackingId, unloadTime),
						new NextExpectedHandlingActivityCalculatedEvent(
								trackingId, RECEIVE, VoyageNumber.none(), CNSHA));

	}

	@Test
	public void throwsWhenAssignedWithUnsatisfiedRoute() throws Throwable {
		final TrackingId trackingId = new TrackingId("1");

		final UnLocode sha = new UnLocode("CNSHA");
		final UnLocode pek = new UnLocode("CNPEK");
		final Date loadTime = new DateTime().withDate(2015, 4, 1)
				.withTimeAtStartOfDay().toDate();
		final Date unloadTime = new DateTime().withDate(2015, 4, 7)
				.withTimeAtStartOfDay().toDate();
		final VoyageNumber voyageNumber = new VoyageNumber("CM001");

		final Itinerary itinerary = new Itinerary(new Leg(voyageNumber, sha,
				pek, loadTime, unloadTime));

		fixture.given(
				new CargoRegisteredEvent(trackingId.getValue(), sha
						.getUnlocode(), pek.getUnlocode(), new DateTime()
						.withDate(2015, 4, 6).withTimeAtStartOfDay().toDate(),
						RoutingStatus.NOT_ROUTED))
				.when(new AssignCargoToRouteCommand(trackingId, itinerary))
				.expectException(CannotAssignCargoToRouteException.class)
				.expectException(
						hasMessage(equalTo("Cannot assign cargo[1] with "
								+ "route specification[origin=CNSHA, destination=CNPEK, arrivalDeadline=2015-04-06 00:00] "
								+ "to route[initialDepartureLocation=CNSHA, finalArrivalLocation=CNPEK, finalArrivalDate=2015-04-07 00:00]")));

	}

	@Test
	public void registersAHandlingEvent() throws Throwable {
		final TrackingId trackingId = new TrackingId("1");

		final UnLocode sha = new UnLocode("CNSHA");
		final UnLocode pek = new UnLocode("CNPEK");
		final Date loadTime = new DateTime().withDate(2015, 4, 1).toDate();
		final Date unloadTime = new DateTime().withDate(2015, 4, 7).toDate();
		final VoyageNumber cm001 = new VoyageNumber("CM001");

		final Date registrationTime = new DateTime().withDate(2015, 4, 2)
				.toDate();
		final Date completionTime = new DateTime().withDate(2015, 4, 2)
				.toDate();

		fixture.given(
				new CargoRegisteredEvent(trackingId.getValue(),
						sha.getUnlocode(), pek.getUnlocode(), new DateTime()
								.withDate(2015, 4, 8).toDate(),
						RoutingStatus.NOT_ROUTED),
				new CargoAssignedEvent(trackingId.getValue(),
						new RouteCandidateDto(Arrays
								.asList(new LegDto(0, "CM001", "CNSHA",
										"CNPEK", loadTime, unloadTime))),
						RoutingStatus.ROUTED.getCode()),
				new CargoEtaCalculatedEvent(trackingId, unloadTime),
				new NextExpectedHandlingActivityCalculatedEvent(trackingId,
						HandlingType.RECEIVE, null, sha))
				.when(new RegisterHandlingEventCommand(completionTime,
						trackingId, sha, HandlingType.RECEIVE, cm001,
						registrationTime))
				.expectEvents(
						new HandlingEventRegisteredEvent(completionTime,
								trackingId, sha, HandlingType.RECEIVE,
								registrationTime),
						new CargoTransportStatusRecalculatedEvent(trackingId,
								TransportStatus.IN_PORT),
						new CargoLastKnownLocationUpdatedEvent(trackingId, sha),
						new CargoCurrentVoyageUpdatedEvent(trackingId, VoyageNumber.none()),
						new NextExpectedHandlingActivityCalculatedEvent(
								trackingId, HandlingType.LOAD, cm001, sha));

	}
}
