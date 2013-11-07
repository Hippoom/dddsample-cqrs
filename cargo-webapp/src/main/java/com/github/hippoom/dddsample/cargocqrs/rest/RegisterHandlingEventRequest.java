package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.Date;

import lombok.Data;

@Data
public class RegisterHandlingEventRequest {
	private String trackingId;
	private String handlingType;
	private String location;
	private String voyageNumber;
	private Date completionTime;

}
