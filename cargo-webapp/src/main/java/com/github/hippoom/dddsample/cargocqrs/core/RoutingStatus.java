package com.github.hippoom.dddsample.cargocqrs.core;

import lombok.Getter;

/**
 * Routing status.
 */
public enum RoutingStatus {
	NOT_ROUTED("0"), ROUTED("1"), MISROUTED("-1");
	@Getter
	private String code;

	private RoutingStatus(String code) {
		this.code = code;
	}

}
