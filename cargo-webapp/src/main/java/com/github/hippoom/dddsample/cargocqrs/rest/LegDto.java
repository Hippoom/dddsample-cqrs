package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

/**
 * DTO for a leg in an itinerary.
 */
@Data
@Embeddable
public class LegDto {
	@Column(name = "idx")
	private int index;
	@Column(name = "voyage_number")
	private String voyageNumber;
	@Column(name = "from_location")
	private String from;
	@Column(name = "to_location")
	private String to;
	@Temporal(value = TemporalType.DATE)
	@Column(name = "load_time")
	private Date loadTime;
	@Temporal(value = TemporalType.DATE)
	@Column(name = "unload_time")
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
	public LegDto(final int index, final String voyageNumber,
			final String from, final String to, Date loadTime, Date unloadTime) {
		this.index = index;
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
