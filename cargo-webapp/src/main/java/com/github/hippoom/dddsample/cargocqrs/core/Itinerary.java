package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An itinerary.
 * 
 */
@EqualsAndHashCode
@ToString
public class Itinerary {

	private List<Leg> legs = new ArrayList<Leg>();

	public Itinerary(Leg leg) {
		legs.add(leg);
	}

	public Itinerary(List<Leg> legs) {
		this.legs = legs;
	}

}
