package com.github.hippoom.dddsample.cargocqrs.pathfinder;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.springframework.integration.test.matcher.PayloadMatcher.hasPayload;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.PollableChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.hippoom.dddsample.cargocqrs.core.Itinerary;
import com.github.hippoom.dddsample.cargocqrs.core.Leg;
import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;
import com.github.hippoom.dddsample.cargocqrs.core.VoyageNumber;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:config.xml", "classpath:pathfinder-translator.xml",
		"classpath:stub-itinerary-translator.xml" })
public class ItineraryTranslatorIntegrationTests {

	@Resource(name = "translateItinerariesInputChannel")
	private MessageChannel in;

	@Resource(name = "fetchRoutesForSpecificationOutputChannel")
	private PollableChannel out;

	@Test
	public void translatesTransitPathToItinerary() throws Throwable {

		final String voyageNumber = "CM001";
		final String fromUnLocode = "CNSHA";
		final String toUnLocode = "CNPEK";
		final Date fromDate = new SimpleDateFormat("yyyy-MM-dd")
				.parse("2014-04-01");
		final Date toDate = new SimpleDateFormat("yyyy-MM-dd")
				.parse("2014-04-02");

		final String payload = "[{\"transitEdges\":[{\"voyageNumber\":\"CM001\",\"fromUnLocode\":\"CNSHA\",\"fromDate\":\"2014-04-01\",\"toUnLocode\":\"CNPEK\",\"toDate\":\"2014-04-02\"}]}]";

		in.send(MessageBuilder.withPayload(payload).build());

		assertThat(out.receive(), hasPayload(hasItem(new Itinerary(new Leg(
				new VoyageNumber(voyageNumber), new UnLocode(fromUnLocode),
				new UnLocode(toUnLocode), fromDate, toDate)))));
	}
}
