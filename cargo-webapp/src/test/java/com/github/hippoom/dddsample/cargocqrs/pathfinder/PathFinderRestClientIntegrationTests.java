package com.github.hippoom.dddsample.cargocqrs.pathfinder;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static org.junit.Assert.assertThat;
import static org.springframework.integration.test.matcher.PayloadMatcher.hasPayload;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.PollableChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runnable;
import com.github.hippoom.dddsample.cargocqrs.config.Configurations;
import com.github.hippoom.dddsample.cargocqrs.core.RouteSpecification;
import com.github.hippoom.dddsample.cargocqrs.core.RouteSpecificationFixture;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:config.xml", "classpath:pathfinder.xml",
		"classpath:stub-pathfinder-client.xml" })
public class PathFinderRestClientIntegrationTests {

	@Autowired
	private Configurations configurations;

	@Resource(name = "fetchRoutesForSpecificationInputChannel")
	private MessageChannel in;

	@Resource(name = "translateTransitPathStringToObjectInputChannel")
	private PollableChannel out;

	@Test
	public void translatesTransitPathToItinerary() throws Throwable {
		final RouteSpecification routeSpec = new RouteSpecificationFixture()
				.build();

		HttpServer server = httpserver(configurations.getPathfinderPort());
		server.get(
				and(by(uri(configurations.getPathFInderRoutesPath())),
						eq(query("spec"), json(routeSpec)))).response("foo");

		running(server, new Runnable() {
			@Override
			public void run() throws ClientProtocolException, IOException {

				in.send(MessageBuilder.withPayload(routeSpec).build());
				assertThat(out.receive(), hasPayload("foo"));
			}
		});

	}

	private String json(RouteSpecification routeSpec) {
		return "{\"origin\":\""
				+ routeSpec.getOrigin().getUnlocode()
				+ "\", \"destination\":\""
				+ routeSpec.getDestination().getUnlocode()
				+ "\", \"arrivalDeadline\":\""
				+ new SimpleDateFormat("yyyy-MM-dd").format(routeSpec
						.getArrivalDeadline()) + "\"}";
	}
}
