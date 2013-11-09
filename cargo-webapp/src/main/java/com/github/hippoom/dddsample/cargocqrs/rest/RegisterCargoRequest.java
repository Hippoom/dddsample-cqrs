package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.Date;

import lombok.Data;

@Data
public class RegisterCargoRequest {

	private String origin;
	private String destination;
	private Date arrivalDeadline;

	public RegisterCargoRequest(String origin, String destination,
			Date arrivalDeadline) {
		this.origin = origin;
		this.destination = destination;
		this.arrivalDeadline = arrivalDeadline;
	}

	public RegisterCargoRequest() {
	}
}
