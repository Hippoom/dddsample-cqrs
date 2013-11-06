package com.github.hippoom.dddsample.cargocqrs.core;

import lombok.Getter;

/**
 * Represents the different transport statuses for a cargo.
 */
public enum TransportStatus {
	NOT_RECEIVED("1"), IN_PORT("2"), ONBOARD_CARRIER("3"), CLAIMED("4"), UNKNOWN(
			"0");

	@Getter
	private String code;

	private TransportStatus(String code) {
		this.code = code;
	}

}
