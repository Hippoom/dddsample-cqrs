package com.github.hippoom.dddsample.cargocqrs.pathfinder;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.RouteSpecification;
import com.github.hippoom.dddsample.cargocqrs.core.RouteSpecificationFixture;
import com.github.hippoom.dddsample.cargocqrs.core.RoutingService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:config.xml", "classpath:pathfinder.xml",
		"classpath:stub-pathfinder.xml" })
public class RoutingServiceIntegrationTests {

	@Autowired
	private RoutingService target;

	@Test
	public void wrapsRoutingServiceAsAnGateway() throws Throwable {

		final RouteSpecification routeSpecification = new RouteSpecificationFixture()
				.build();
		final List<Itinerary> itineraries = target
				.fetchRoutesForSpecification(routeSpecification);

		assertThat(itineraries, not(nullValue()));
	}
}
