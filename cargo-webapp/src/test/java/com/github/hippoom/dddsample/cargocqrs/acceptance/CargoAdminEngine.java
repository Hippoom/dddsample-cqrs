package com.github.hippoom.dddsample.cargocqrs.acceptance;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

public class CargoAdminEngine {

	private WebApplicationContext wac;

	private MockMvc mockMvc() {
		return webAppContextSetup(this.wac).build();
	}
}
