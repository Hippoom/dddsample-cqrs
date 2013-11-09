package com.github.hippoom.dddsample.cargocqrs.pathfinder;

import java.text.SimpleDateFormat;

import com.github.hippoom.dddsample.cargocqrs.core.RouteSpecification;

public class FetchRouteRequestTranslator {

	public String origin(RouteSpecification routeSpec) {
		return routeSpec.getOrigin().getUnlocode();
	}

	public String destination(RouteSpecification routeSpec) {
		return routeSpec.getDestination().getUnlocode();
	}

	public String arrivalDeadline(RouteSpecification routeSpec) {
		return new SimpleDateFormat("yyyy-MM-dd").format(routeSpec
				.getArrivalDeadline());
	}

}
