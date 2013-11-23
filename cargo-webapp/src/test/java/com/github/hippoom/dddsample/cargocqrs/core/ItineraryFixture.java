package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.ArrayList;
import java.util.List;

public class ItineraryFixture {

	List<LegFixture> legs = new ArrayList<LegFixture>();

	public ItineraryFixture add(LegFixture leg) {
		this.legs.add(leg);
		return this;
	}

	public Itinerary build() {
		List<Leg> legs = new ArrayList<Leg>();
		for (LegFixture leg : this.legs) {
			legs.add(leg.build());
		}
		return new Itinerary(legs);
	}

}
