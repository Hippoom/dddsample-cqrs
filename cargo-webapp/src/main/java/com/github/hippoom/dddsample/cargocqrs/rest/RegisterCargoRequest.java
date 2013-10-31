package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.Date;

import lombok.Data;

@Data
public class RegisterCargoRequest {
	private String origin;
	private String destination;
	private Date arrivalDeadline;
}
