package com.github.hippoom.dddsample.cargo.core;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class TrackingId {
	private String value;

	public TrackingId(String value) {
		this.value = value;
	}

}
