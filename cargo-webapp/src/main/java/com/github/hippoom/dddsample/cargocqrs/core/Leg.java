package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.lang.Validate;

/**
 * An itinerary consists of one or more legs.
 */
@EqualsAndHashCode
@ToString
@Getter
public class Leg {

	private VoyageNumber voyageNumber;
	private UnLocode loadLocation;
	private UnLocode unloadLocation;
	private Date loadTime;
	private Date unloadTime;

	public Leg(VoyageNumber voyage, UnLocode loadLocation,
			UnLocode unloadLocation, Date loadTime, Date unloadTime) {
		Validate.noNullElements(new Object[] { voyage, loadLocation,
				unloadLocation, loadTime, unloadTime });

		this.voyageNumber = voyage;
		this.loadLocation = loadLocation;
		this.unloadLocation = unloadLocation;
		this.loadTime = loadTime;
		this.unloadTime = unloadTime;
	}

}
