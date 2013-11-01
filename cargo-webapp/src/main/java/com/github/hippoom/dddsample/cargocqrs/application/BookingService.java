package com.github.hippoom.dddsample.cargocqrs.application;

import java.util.Date;
import java.util.List;

import lombok.Setter;

import com.github.hippoom.dddsample.cargocqrs.axon.GenericCommandGateway;
import com.github.hippoom.dddsample.cargocqrs.command.RegisterCargoCommand;
import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.RouteSpecification;
import com.github.hippoom.dddsample.cargocqrs.core.RoutingService;
import com.github.hippoom.dddsample.cargocqrs.core.TrackingId;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;

public class BookingService {
	@Setter
	private GenericCommandGateway commandGateway;
	@Setter
	private CargoDetailQueryService cargoDetailQueryService;
	@Setter
	private RoutingService routingService;

	/**
	 * Registers a new cargo in the tracking system, not yet routed.
	 * 
	 * @param origin
	 *            cargo origin
	 * @param destination
	 *            cargo destination
	 * @param arrivalDeadline
	 *            arrival deadline
	 * @return Cargo tracking id
	 */
	public TrackingId register(UnLocode originUnLocode,
			UnLocode destinationUnLocode, Date arrivalDeadline) {
		return commandGateway.sendAndWait(new RegisterCargoCommand(
				originUnLocode, destinationUnLocode, arrivalDeadline));
	}

	/**
	 * Requests a list of itineraries describing possible routes for this cargo.
	 * 
	 * @param trackingId
	 *            cargo tracking id
	 * @return A list of possible itineraries for this cargo
	 */
	public List<Itinerary> requestPossibleRoutesForCargo(TrackingId trackingId) {
		final CargoDto cargo = cargoDetailQueryService.findBy(trackingId
				.getValue());
		return routingService
				.fetchRoutesForSpecification(routeSpecificationOf(cargo));
	}

	private RouteSpecification routeSpecificationOf(CargoDto cargo) {
		return new RouteSpecification(new UnLocode(cargo.getOriginUnlocode()),
				new UnLocode(cargo.getDestinationUnlocode()),
				cargo.getArrivalDeadline());
	}
}
