package com.github.hippoom.dddsample.cargocqrs.acceptance;

import org.junit.runner.RunWith;

import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@Cucumber.Options(features = { "classpath:" }, format = {
		"html:target/acceptance-cucumber-html",
		"json:target/acceptance-cucumber-json" }, name = "Operator registers a handling event of which type is LOAD")
public class SpecificAcceptanceTests {

}