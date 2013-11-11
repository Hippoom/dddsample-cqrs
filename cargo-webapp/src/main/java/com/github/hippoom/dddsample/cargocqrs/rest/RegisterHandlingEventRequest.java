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

	public RegisterHandlingEventRequest(String trackingId, String handlingType,
			String location, String voyageNumber, Date completionTime) {
		this.trackingId = trackingId;
		this.handlingType = handlingType;
		this.location = location;
		this.voyageNumber = voyageNumber;
		this.completionTime = completionTime;
	}

	public RegisterHandlingEventRequest() {
	}

}
