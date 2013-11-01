package com.github.hippoom.dddsample.cargocqrs.core;

import org.joda.time.DateTime;

public class RouteSpecificationFixture {

	public RouteSpecification build() {
		return new RouteSpecification(new UnLocode("CNSHA"), new UnLocode(
				"CNPEK"), new DateTime().withDate(2014, 4, 7).toDate());
	}

}
