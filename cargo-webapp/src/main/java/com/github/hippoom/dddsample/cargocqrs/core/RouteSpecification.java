package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.lang.Validate;

/**
 * Route specification. Describes where a cargo orign and destination is, and
 * the arrival deadline.
 * 
 */
@EqualsAndHashCode
@ToString
@Getter
public class RouteSpecification {

	private UnLocode origin;
	private UnLocode destination;
	private Date arrivalDeadline;

	/**
	 * @param origin
	 *            origin location - can't be the same as the destination
	 * @param destination
	 *            destination location - can't be the same as the origin
	 * @param arrivalDeadline
	 *            arrival deadline
	 */
	public RouteSpecification(final UnLocode origin,
			final UnLocode destination, final Date arrivalDeadline) {
		Validate.notNull(origin, "Origin is required");
		Validate.notNull(destination, "Destination is required");
		Validate.notNull(arrivalDeadline, "Arrival deadline is required");
		Validate.isTrue(!origin.equals(destination),
				"Origin and destination can't be the same: " + origin);

		this.origin = origin;
		this.destination = destination;
		this.arrivalDeadline = (Date) arrivalDeadline.clone();
	}

	/**
	 * 
	 * @param itinerary
	 * @return
	 */
	public boolean isSatisfiedBy(final Itinerary itinerary) {
		return itinerary != null
				&& origin.equals(itinerary.initialDepartureLocation())
				&& destination.equals(itinerary.finalArrivalLocation())
				&& arrivalDeadline.after(itinerary.finalArrivalDate());
	}

}
