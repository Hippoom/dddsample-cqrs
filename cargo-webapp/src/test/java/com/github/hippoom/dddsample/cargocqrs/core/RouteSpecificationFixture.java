package com.github.hippoom.dddsample.cargocqrs.core;

import org.joda.time.DateTime;

public class RouteSpecificationFixture {

	private UnLocode origin = UnLocodes.CNSHA;
	private UnLocode destination = UnLocodes.CNPEK;
	private DateTime arrivalDeadline = new DateTime().withDate(2014, 4, 7)
			.withTimeAtStartOfDay();

	public RouteSpecificationFixture origin(UnLocode origin) {
		this.origin = origin;
		return this;
	}

	public RouteSpecificationFixture destination(UnLocode destination) {
		this.destination = destination;
		return this;
	}

	public RouteSpecificationFixture arrivalBefore(int year, int monthOfYear,
			int dateOfMonth) {
		this.arrivalDeadline = arrivalDeadline.withYear(year)
				.withMonthOfYear(monthOfYear).withDayOfMonth(dateOfMonth);
		return this;
	}

	public RouteSpecification build() {
		return new RouteSpecification(origin, destination,
				arrivalDeadline.toDate());
	}

}
