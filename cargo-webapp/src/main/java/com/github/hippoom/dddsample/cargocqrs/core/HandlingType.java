package com.github.hippoom.dddsample.cargocqrs.core;

import lombok.Getter;

public enum HandlingType {
	LOAD("1"), UNLOAD("2"), RECEIVE("3"), CLAIM("4"), CUSTOMS("5");
	@Getter
	private final String code;

	private HandlingType(String code) {
		this.code = code;
	}

}