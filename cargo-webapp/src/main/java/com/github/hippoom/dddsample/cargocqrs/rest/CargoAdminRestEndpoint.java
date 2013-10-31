package com.github.hippoom.dddsample.cargocqrs.rest;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.hippoom.dddsample.cargocqrs.application.BookingService;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.query.CargoDetailQueryService;

@Slf4j
@Controller
public class CargoAdminRestEndpoint {
	@Resource(name = "bookingService")
	private BookingService bookingService;
	@Resource(name = "cargoDetailQueryService")
	private CargoDetailQueryService cargoDetailQueryService;

	@RequestMapping(value = "/cargo", method = RequestMethod.PUT)
	@ResponseBody
	public String handle(@RequestBody RegisterCargoRequest request) {
		final TrackingId trackingId = bookingService.register(
				with(request.getOrigin()), with(request.getDestination()),
				with(request.getArrivalDeadline()));
		return trackingId.getValue();
	}

	@RequestMapping(value = "/cargo/{trackingId}", method = RequestMethod.GET)
	@ResponseBody
	public CargoDto findCargoBy(@PathVariable("trackingId") String trackingId) {
		final CargoDto cargo = cargoDetailQueryService.findBy(trackingId);
		if (cargo == null) {
			throw new ResourceNotFoundException(
					"Cannot find cargo by trackingId[" + trackingId + "].");
		}
		return cargo;
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseBody
	protected void handleUnrecognizedThrown(
			final ResourceNotFoundException thrown,
			final HttpServletResponse response) throws IOException {
		logAndReturn(HttpStatus.NOT_FOUND, thrown, response);
	}

	private void logAndReturn(final HttpStatus statusCode,
			final Throwable thrown, final HttpServletResponse response)
			throws IOException {
		log.error(thrown.getMessage(), thrown);
		response.sendError(statusCode.value(), thrown.getMessage());
	}

	@ExceptionHandler(Throwable.class)
	@ResponseBody
	protected void handleUnrecognizedThrown(final Throwable thrown,
			final HttpServletResponse response) throws IOException {
		logAndReturn(HttpStatus.BAD_REQUEST, thrown, response);
	}

	private UnLocode with(String unlocode) {
		return new UnLocode(unlocode);
	}

	private Date with(Date arrivalDeadline) {
		return arrivalDeadline;
	}
}
