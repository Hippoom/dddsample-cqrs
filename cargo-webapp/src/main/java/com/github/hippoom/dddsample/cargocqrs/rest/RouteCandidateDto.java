package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.List;

import lombok.Data;

/**
 * DTO for presenting and selecting an itinerary from a collection of
 * candidates.
 */
@Data
public class RouteCandidateDto {

	private List<LegDto> legs;

	/**
	 * Constructor.
	 * 
	 * @param legs
	 *            The legs for this itinerary.
	 */
	public RouteCandidateDto(final List<LegDto> legs) {
		this.legs = legs;
	}

	/**
	 * frameworks only
	 */
	private RouteCandidateDto() {
	}

}