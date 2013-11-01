package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.Date;

import lombok.Data;

/**
 * DTO for a leg in an itinerary.
 */
@Data
public class LegDto {

	private String voyageNumber;
	private String from;
	private String to;
	private Date loadTime;
	private Date unloadTime;

	/**
	 * Constructor.
	 * 
	 * @param voyageNumber
	 * @param from
	 * @param to
	 * @param loadTime
	 * @param unloadTime
	 */
	public LegDto(final String voyageNumber, final String from,
			final String to, Date loadTime, Date unloadTime) {
		this.voyageNumber = voyageNumber;
		this.from = from;
		this.to = to;
		this.loadTime = loadTime;
		this.unloadTime = unloadTime;
	}

	/**
	 * frameworks only
	 */
	private LegDto() {
	}

}
