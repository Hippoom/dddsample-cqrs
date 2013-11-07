package com.github.hippoom.dddsample.cargocqrs.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.hippoom.dddsample.cargocqrs.application.HandlingApplication;
import com.github.hippoom.dddsample.cargocqrs.core.HandlingType;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;

@Slf4j
@Controller
public class HandlingRestEndpoint {
	@Autowired
	private HandlingApplication handingApplication;

	@RequestMapping(value = "/handlingevent", method = RequestMethod.PUT)
	public void handle(@RequestBody RegisterHandlingEventRequest request) {

		handingApplication.registerHandlingEvent(request.getCompletionTime(),
				TrackingId.of(request.getTrackingId()),
				new UnLocode(request.getLocation()),
				HandlingType.of(request.getHandlingType()));
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

}
