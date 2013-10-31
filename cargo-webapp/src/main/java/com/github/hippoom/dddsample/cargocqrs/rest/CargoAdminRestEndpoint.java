package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.Date;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.hippoom.dddsample.cargocqrs.application.BookingService;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;

@Slf4j
@Controller
public class CargoAdminRestEndpoint {
	@Resource(name = "bookingService")
	private BookingService bookingService;

	@RequestMapping(value = "/cargo", method = RequestMethod.PUT)
	@ResponseBody
	public RegisterCargoResponse handle(
			@RequestBody RegisterCargoRequest request) {
		final TrackingId trackingId = bookingService.register(
				with(request.getOrigin()), with(request.getDestination()),
				with(request.getArrivalDeadline()));
		return RegisterCargoResponse.success(trackingId.getValue());
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	protected RegisterCargoResponse handleUnrecognizedThrown(
			final Throwable thrown) {
		log.error(thrown.getMessage(), thrown);
		return RegisterCargoResponse.failure(thrown);
	}

	private UnLocode with(String unlocode) {
		return new UnLocode(unlocode);
	}

	private Date with(Date arrivalDeadline) {
		return arrivalDeadline;
	}
}
