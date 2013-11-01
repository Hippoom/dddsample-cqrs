package com.pathfinder.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class TransitPath {

	private List<TransitEdge> transitEdges;

	/**
	 * Constructor.
	 * 
	 * @param transitEdges
	 *            The legs for this itinerary.
	 */
	public TransitPath(final List<TransitEdge> transitEdges) {
		this.transitEdges = transitEdges;
	}

	public TransitPath(TransitEdge transitEdge) {
		this.transitEdges = new ArrayList<TransitEdge>();
		this.transitEdges.add(transitEdge);
	}

	/**
	 * @return An unmodifiable list DTOs.
	 */
	public List<TransitEdge> getTransitEdges() {
		return Collections.unmodifiableList(transitEdges);
	}

	/**
	 * frameworks only
	 */
	private TransitPath() {

	}
}
