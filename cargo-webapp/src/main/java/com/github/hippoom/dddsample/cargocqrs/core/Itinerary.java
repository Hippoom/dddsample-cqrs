package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * An itinerary.
 * 
 */
@EqualsAndHashCode
@ToString
public class Itinerary {
	public static final Itinerary EMPTY_ITINERARY = new Itinerary(
			new ArrayList<Leg>());
	private static final Date END_OF_DAYS = new Date(Long.MAX_VALUE);
	@Getter
	private List<Leg> legs = new ArrayList<Leg>();

	public Itinerary(Leg leg) {
		legs.add(leg);
	}

	public Itinerary(Leg... legs) {
		for (Leg leg : legs) {
			this.legs.add(leg);
		}
	}

	public Itinerary(List<Leg> legs) {
		this.legs = legs;
	}

	/**
	 * @return The initial departure location.
	 */
	public UnLocode initialDepartureLocation() {
		if (legs.isEmpty()) {
			return UnLocode.UNKNOWN;
		} else {
			return legs.get(0).getLoadLocation();
		}
	}

	/**
	 * @return The final arrival location.
	 */
	public UnLocode finalArrivalLocation() {
		if (legs.isEmpty()) {
			return UnLocode.UNKNOWN;
		} else {
			return lastLeg().getUnloadLocation();
		}
	}

	/**
	 * @return Date when cargo arrives at final destination.
	 */
	public Date finalArrivalDate() {
		final Leg lastLeg = lastLeg();

		if (lastLeg == null) {
			return new Date(END_OF_DAYS.getTime());
		} else {
			return new Date(lastLeg.getUnloadTime().getTime());
		}
	}

	/**
	 * @return The last leg on the itinerary.
	 */
	public Leg lastLeg() {
		if (legs.isEmpty()) {
			return null;
		} else {
			return legs.get(legs.size() - 1);
		}
	}

}
