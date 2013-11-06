package com.github.hippoom.dddsample.cargocqrs.pathfinder;

import java.util.ArrayList;
import java.util.List;

import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.Leg;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber;
import com.pathfinder.api.TransitEdge;
import com.pathfinder.api.TransitPath;

public class ItineraryTranslator {

	public List<Itinerary> from(List<TransitPath> transitPaths) {
		final List<Itinerary> itineraries = new ArrayList<Itinerary>();

		for (TransitPath transitPath : transitPaths) {
			final List<Leg> legs = new ArrayList<Leg>();
			for (TransitEdge transitEdge : transitPath.getTransitEdges()) {
				legs.add(new Leg(
						new VoyageNumber(transitEdge.getVoyageNumber()),
						new UnLocode(transitEdge.getFromUnLocode()),
						new UnLocode(transitEdge.getToUnLocode()), transitEdge
								.getFromDate(), transitEdge.getToDate()));
			}
			itineraries.add(new Itinerary(legs));
		}
		return itineraries;
	}
}
