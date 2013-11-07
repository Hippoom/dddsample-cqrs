package com.github.hippoom.dddsample.cargocqrs.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

public class DeliveryUnitTests {

	private UnLocode sha = new UnLocode("CNSHA");
	private UnLocode pek = new UnLocode("CNPEK");
	private VoyageNumber cm001 = new VoyageNumber("CM001");

	@Test
	public void givenTheFirstReceiveHandlingEvent() throws Throwable {

		final RouteSpecification routeSpecification = new RouteSpecification(
				sha, pek, new DateTime().withDate(2014, 4, 7)
						.withTimeAtStartOfDay().toDate());
		final Date unloadTime = new DateTime().withDate(2014, 4, 3)
				.withTimeAtStartOfDay().toDate();
		final Itinerary itinerary = new Itinerary(new Leg(cm001, sha, pek,
				new DateTime().withDate(2014, 4, 2).withTimeAtStartOfDay()
						.toDate(), unloadTime));
		final HandlingEvent handlingEvent = new HandlingEvent(
				new HandlingActivity(HandlingType.RECEIVE, sha, cm001),
				unloadTime, new Date());

		Delivery delivery = Delivery.derivedFrom(routeSpecification, itinerary,
				handlingEvent);

		assertThat(delivery.routingStatus(), equalTo(RoutingStatus.ROUTED));
		assertThat(delivery.transportStatus(), equalTo(TransportStatus.IN_PORT));
		assertThat(delivery.nextExpectedHandlingActivity(),
				equalTo(new HandlingActivity(HandlingType.LOAD, sha, cm001)));
		assertThat(delivery.lastKnownLocation(), equalTo(sha));

	}

	@Test
	public void givenTheFirstLoadHandlingEvent() throws Throwable {

		final RouteSpecification routeSpecification = new RouteSpecification(
				sha, pek, new DateTime().withDate(2014, 4, 7)
						.withTimeAtStartOfDay().toDate());
		final Date unloadTime = new DateTime().withDate(2014, 4, 3)
				.withTimeAtStartOfDay().toDate();
		final Itinerary itinerary = new Itinerary(new Leg(cm001, sha, pek,
				new DateTime().withDate(2014, 4, 2).withTimeAtStartOfDay()
						.toDate(), unloadTime));
		final HandlingEvent handlingEvent = new HandlingEvent(
				new HandlingActivity(HandlingType.LOAD, sha, cm001),
				unloadTime, new Date());

		Delivery delivery = Delivery.derivedFrom(routeSpecification, itinerary,
				handlingEvent);

		assertThat(delivery.routingStatus(), equalTo(RoutingStatus.ROUTED));
		assertThat(delivery.transportStatus(),
				equalTo(TransportStatus.ONBOARD_CARRIER));
		assertThat(delivery.nextExpectedHandlingActivity(),
				equalTo(new HandlingActivity(HandlingType.UNLOAD, pek, cm001)));
		assertThat(delivery.lastKnownLocation(), equalTo(sha));
		assertThat(delivery.currentVoyage(), equalTo(cm001));
	}
}