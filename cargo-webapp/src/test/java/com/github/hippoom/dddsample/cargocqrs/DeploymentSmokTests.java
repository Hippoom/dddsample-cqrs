package com.github.hippoom.dddsample.cargocqrs;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

public class DeploymentSmokTests {

	@Test
	public void showsWelcomeGivenDeploymentDone() throws Throwable {
		assertThat(getStatusCodeFromIndexPage(), equalTo(200));
	}

	private int getStatusCodeFromIndexPage() throws IOException,
			ClientProtocolException {
		return Request.Get("http://localhost:9999/cargo/index.jsp").execute()
				.returnResponse().getStatusLine().getStatusCode();

	}
}
