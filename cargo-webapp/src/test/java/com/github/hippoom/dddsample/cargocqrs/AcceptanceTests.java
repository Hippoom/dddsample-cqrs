package com.github.hippoom.dddsample.cargocqrs;

import org.junit.runner.RunWith;

import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@Cucumber.Options(features = { "classpath:" }, format = { "html:target/acceptance-cucumber-html-report" })
public class AcceptanceTests {

}
