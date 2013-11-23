package com.github.hippoom.dddsample.cargocqrs.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.lang.Validate;

/**
 * Identifies a voyage.
 * 
 */
@EqualsAndHashCode
@ToString
public class VoyageNumber {

	@Getter
	private String number;

	public VoyageNumber(String number) {
		Validate.notNull(number);
		this.number = number;
	}

	public static VoyageNumber none() {
		return new VoyageNumber("");
	}

	public boolean exists() {
		return this.equals(none());
	}
}
