package com.github.hippoom.dddsample.cargocqrs.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

public class RouteSpecificationUnitTests {

	@Test
	public void returnsTrue() throws Throwable {
		final UnLocode origin = new UnLocode("CNSHA");
		final UnLocode destination = new UnLocode("CNPEK");
		final DateTime tomorrow = DateTime.now().plusDays(1);
		final DateTime aWeekLater = DateTime.now().plusDays(7);
		final DateTime twoWeekLater = DateTime.now().plusDays(14);
		final Itinerary itinerary = new Itinerary(new Leg(new VoyageNumber(
				"CM001"), origin, destination, tomorrow.toDate(),
				aWeekLater.toDate()));

		final RouteSpecification spec = new RouteSpecification(origin,
				destination, twoWeekLater.toDate());

		assertThat(spec.isSatisfiedBy(itinerary), is(true));
	}

	@Test
	public void returnsFalseIfOriginUnmatched() throws Throwable {
		final UnLocode sha = new UnLocode("CNSHA");
		final UnLocode pek = new UnLocode("CNPEK");
		final DateTime tomorrow = DateTime.now().plusDays(1);
		final DateTime aWeekLater = DateTime.now().plusDays(7);
		final DateTime twoWeekLater = DateTime.now().plusDays(14);
		final Itinerary itinerary = new Itinerary(new Leg(new VoyageNumber(
				"CM001"), new UnLocode("CNTAN"), pek, tomorrow.toDate(),
				aWeekLater.toDate()));

		final RouteSpecification spec = new RouteSpecification(sha, pek,
				twoWeekLater.toDate());

		assertThat(spec.isSatisfiedBy(itinerary), is(false));
	}

	@Test
	public void returnsFalseIfDestinationUnmatched() throws Throwable {
		final UnLocode sha = new UnLocode("CNSHA");
		final UnLocode pek = new UnLocode("CNPEK");
		final DateTime tomorrow = DateTime.now().plusDays(1);
		final DateTime aWeekLater = DateTime.now().plusDays(7);
		final DateTime twoWeekLater = DateTime.now().plusDays(14);
		final Itinerary itinerary = new Itinerary(new Leg(new VoyageNumber(
				"CM001"), sha, new UnLocode("CNTAN"), tomorrow.toDate(),
				aWeekLater.toDate()));

		final RouteSpecification spec = new RouteSpecification(sha, pek,
				twoWeekLater.toDate());

		assertThat(spec.isSatisfiedBy(itinerary), is(false));
	}

	@Test
	public void returnsFalseIfArrivalDeadlineUnmatched() throws Throwable {
		final UnLocode sha = new UnLocode("CNSHA");
		final UnLocode pek = new UnLocode("CNPEK");
		final DateTime tomorrow = DateTime.now().plusDays(1);
		final DateTime aWeekLater = DateTime.now().plusDays(7);
		final Itinerary itinerary = new Itinerary(new Leg(new VoyageNumber(
				"CM001"), sha, pek, tomorrow.toDate(), aWeekLater.toDate()));

		final RouteSpecification spec = new RouteSpecification(sha, pek,
				aWeekLater.toDate());

		assertThat(spec.isSatisfiedBy(itinerary), is(false));
	}

}
