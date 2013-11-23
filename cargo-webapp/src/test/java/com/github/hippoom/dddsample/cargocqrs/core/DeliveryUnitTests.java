package com.github.hippoom.dddsample.cargocqrs.core;

import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.CLAIM;
import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.LOAD;
import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.RECEIVE;
import static com.github.hippoom.dddsample.cargocqrs.core.HandlingType.UNLOAD;
import static com.github.hippoom.dddsample.cargocqrs.core.RoutingStatus.ROUTED;
import static com.github.hippoom.dddsample.cargocqrs.core.TransportStatus.CLAIMED;
import static com.github.hippoom.dddsample.cargocqrs.core.TransportStatus.IN_PORT;
import static com.github.hippoom.dddsample.cargocqrs.core.TransportStatus.ONBOARD_CARRIER;
import static com.github.hippoom.dddsample.cargocqrs.core.UnLocodes.CNPEK;
import static com.github.hippoom.dddsample.cargocqrs.core.UnLocodes.CNSHA;
import static com.github.hippoom.dddsample.cargocqrs.core.UnLocodes.CNTAN;
import static com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber.none;
import static com.github.hippoom.dddsample.cargocqrs.core.VoyageNumbers.CM001;
import static com.github.hippoom.dddsample.cargocqrs.core.VoyageNumbers.CM002;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DeliveryUnitTests {

	@Test
	public void givenTheFirstReceiveHandlingEvent() throws Throwable {

		final RouteSpecification routeSpecification = new RouteSpecificationFixture()
				.origin(CNSHA).destination(CNPEK).arrivalBefore(2014, 4, 7)
				.build();

		final Itinerary itinerary = new ItineraryFixture()
				.add(new LegFixture(CNSHA, CNPEK).voyage(CM001).unloadAt(2014,
						4, 2)).build();

		final HandlingEvent handlingEvent = new HandlingEventFixture(RECEIVE,
				CNSHA).completeAt(2014, 4, 2).build();

		Delivery delivery = Delivery.derivedFrom(routeSpecification, itinerary,
				handlingEvent);

		assertThat(delivery.routingStatus(), is(ROUTED));
		assertThat(delivery.transportStatus(), is(IN_PORT));
		assertThat(delivery.nextExpectedHandlingActivity(),
				equalTo(new HandlingActivity(LOAD, CNSHA, CM001)));
		assertThat(delivery.lastKnownLocation(), is(CNSHA));
		assertThat(delivery.currentVoyage(), is(none()));

	}

	@Test
	public void givenTheFirstLoadHandlingEvent() throws Throwable {

		final RouteSpecification routeSpecification = new RouteSpecificationFixture()
				.origin(CNSHA).destination(CNPEK).arrivalBefore(2014, 4, 7)
				.build();

		final Itinerary itinerary = new ItineraryFixture()
				.add(new LegFixture(CNSHA, CNPEK).voyage(CM001).unloadAt(2014,
						4, 2)).build();

		final HandlingEvent handlingEvent = new HandlingEventFixture(LOAD,
				CNSHA).voyage(CM001).completeAt(2014, 4, 2).build();

		Delivery delivery = Delivery.derivedFrom(routeSpecification, itinerary,
				handlingEvent);

		assertThat(delivery.routingStatus(), is(ROUTED));
		assertThat(delivery.transportStatus(), is(ONBOARD_CARRIER));
		assertThat(delivery.nextExpectedHandlingActivity(),
				equalTo(new HandlingActivity(UNLOAD, CNPEK, CM001)));
		assertThat(delivery.lastKnownLocation(), is(CNSHA));
		assertThat(delivery.currentVoyage(), equalTo(CM001));
	}

	@Test
	public void givenTheFirstUnloadHandlingEvent() throws Throwable {

		final RouteSpecification routeSpecification = new RouteSpecificationFixture()
				.origin(CNSHA).destination(CNPEK).arrivalBefore(2014, 4, 7)
				.build();

		final Itinerary itinerary = new ItineraryFixture()
				.add(new LegFixture(CNSHA, CNTAN).voyage(CM001).unloadAt(2014,
						4, 2))
				.add(new LegFixture(CNTAN, CNPEK).voyage(CM002).unloadAt(2014,
						4, 3)).build();

		final HandlingEvent handlingEvent = new HandlingEventFixture(UNLOAD,
				CNTAN).voyage(CM001).completeAt(2014, 4, 2).build();

		Delivery delivery = Delivery.derivedFrom(routeSpecification, itinerary,
				handlingEvent);

		assertThat(delivery.routingStatus(), is(ROUTED));
		assertThat(delivery.transportStatus(), is(IN_PORT));
		assertThat(delivery.lastKnownLocation(), is(CNTAN));
		assertThat(delivery.currentVoyage(), is(none()));
		assertThat(delivery.nextExpectedHandlingActivity(),
				equalTo(new HandlingActivity(LOAD, CNTAN, CM002)));

	}

	@Test
	public void givenTheLastUnloadHandlingEvent() throws Throwable {

		final RouteSpecification routeSpecification = new RouteSpecificationFixture()
				.origin(CNSHA).destination(CNPEK).arrivalBefore(2014, 4, 7)
				.build();

		final Itinerary itinerary = new ItineraryFixture()
				.add(new LegFixture(CNSHA, CNTAN).voyage(CM001).unloadAt(2014,
						4, 2))
				.add(new LegFixture(CNTAN, CNPEK).voyage(CM002).unloadAt(2014,
						4, 3)).build();

		final HandlingEvent handlingEvent = new HandlingEventFixture(UNLOAD,
				CNPEK).voyage(CM002).completeAt(2014, 4, 3).build();

		Delivery delivery = Delivery.derivedFrom(routeSpecification, itinerary,
				handlingEvent);

		assertThat(delivery.routingStatus(), is(ROUTED));
		assertThat(delivery.transportStatus(), is(IN_PORT));
		assertThat(delivery.lastKnownLocation(), is(CNPEK));
		assertThat(delivery.currentVoyage(), is(none()));
		assertThat(delivery.nextExpectedHandlingActivity(),
				equalTo(new HandlingActivity(CLAIM, CNPEK, none())));
		assertThat(delivery.isUnloadedAtDestination(), is(true));
	}

	@Test
	public void givenTheClaimedHandlingEvent() throws Throwable {

		final RouteSpecification routeSpecification = new RouteSpecificationFixture()
				.origin(CNSHA).destination(CNPEK).arrivalBefore(2014, 4, 7)
				.build();

		final Itinerary itinerary = new ItineraryFixture()
				.add(new LegFixture(CNSHA, CNTAN).voyage(CM001).unloadAt(2014,
						4, 2))
				.add(new LegFixture(CNTAN, CNPEK).voyage(CM002).unloadAt(2014,
						4, 3)).build();

		final HandlingEvent handlingEvent = new HandlingEventFixture(CLAIM,
				CNPEK).completeAt(2014, 4, 4).build();

		Delivery delivery = Delivery.derivedFrom(routeSpecification, itinerary,
				handlingEvent);

		assertThat(delivery.routingStatus(), is(ROUTED));
		assertThat(delivery.transportStatus(), is(CLAIMED));
		assertThat(delivery.lastKnownLocation(), is(CNPEK));
		assertThat(delivery.currentVoyage(), is(none()));
		assertThat(delivery.nextExpectedHandlingActivity(), is(nullValue()));
		assertThat(delivery.isUnloadedAtDestination(), is(false));
	}
}
