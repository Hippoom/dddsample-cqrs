package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.List;

import lombok.Data;

/**
 * DTO for presenting and selecting an itinerary from a collection of
 * candidates.
 */
@Data
public class RouteCandidateDto {

	private List<LegDTO> legs;

	/**
	 * Constructor.
	 * 
	 * @param legs
	 *            The legs for this itinerary.
	 */
	public RouteCandidateDto(final List<LegDTO> legs) {
		this.legs = legs;
	}

	/**
	 * frameworks only
	 */
	private RouteCandidateDto() {
	}

}