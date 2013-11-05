package com.github.hippoom.dddsample.cargocqrs.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class TrackingId {
	@Getter
	private String value;

	public TrackingId(String value) {
		this.value = value;
	}

	public static TrackingId of(String value) {
		return new TrackingId(value);
	}

	public static TrackingId of(Number value) {
		return new TrackingId(String.valueOf(value));
	}

	/**
	 * return value directly, it'll be used for persistence of axon's event 
	 */
	@Override
	public String toString() {
		return value;
	}
}
