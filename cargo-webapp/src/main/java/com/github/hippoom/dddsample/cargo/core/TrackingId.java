package com.github.hippoom.dddsample.cargo.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class TrackingId {
	@Getter
	private String value;

	public TrackingId(String value) {
		this.value = value;
	}

}
