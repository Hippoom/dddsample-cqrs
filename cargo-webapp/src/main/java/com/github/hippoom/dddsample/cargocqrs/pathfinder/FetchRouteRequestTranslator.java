package com.github.hippoom.dddsample.cargocqrs.pathfinder;

import java.text.SimpleDateFormat;

import com.github.hippoom.dddsample.cargocqrs.core.RouteSpecification;

public class FetchRouteRequestTranslator {

	public String from(RouteSpecification routeSpec) {
		return json(routeSpec);
	}

	private String json(RouteSpecification routeSpec) {
		return "{\"origin\":\""
				+ routeSpec.getOrigin().getUnlocode()
				+ "\", \"destination\":\""
				+ routeSpec.getDestination().getUnlocode()
				+ "\", \"arrivalDeadline\":\""
				+ new SimpleDateFormat("yyyy-MM-dd").format(routeSpec
						.getArrivalDeadline()) + "\"}";
	}
}
