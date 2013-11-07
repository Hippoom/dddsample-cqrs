package com.github.hippoom.dddsample.cargocqrs.time;

import java.util.Date;

public class SimpleClock implements Clock {

	@Override
	public Date now() {
		return new Date();
	}

}
