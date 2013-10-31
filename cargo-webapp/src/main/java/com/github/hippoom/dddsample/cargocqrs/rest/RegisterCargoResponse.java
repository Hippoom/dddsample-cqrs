package com.github.hippoom.dddsample.cargocqrs.rest;

import lombok.Data;

@Data
public class RegisterCargoResponse {

	private String statusCode;
	private String message;
	private String trackingId;

	private RegisterCargoResponse(String statusCode, String message,
			String trackingId) {
		this.statusCode = statusCode;
		this.message = message;
		this.trackingId = trackingId;
	}

	public static RegisterCargoResponse success(String trackingId) {
		return new RegisterCargoResponse(StatusCodes.SUCCESS, "", trackingId);
	}

	public static RegisterCargoResponse failure(Throwable thrown) {
		return new RegisterCargoResponse(StatusCodes.UNKNOWN_ERROR,
				thrown.getMessage(), null);
	}

}
