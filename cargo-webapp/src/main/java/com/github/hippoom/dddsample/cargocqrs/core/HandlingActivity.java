package com.github.hippoom.dddsample.cargocqrs.core;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.lang.Validate;

/**
 * A handling activity represents how and where a cargo can be handled, and can
 * be used to express predictions about what is expected to happen to a cargo in
 * the future.
 * 
 */
@EqualsAndHashCode
@ToString
public class HandlingActivity {

	private HandlingType type;
	private UnLocode location;
	private VoyageNumber voyage;

	public HandlingActivity(final HandlingType type, final UnLocode location) {
		this(type, location, null);
	}

	public HandlingActivity(final HandlingType type, final UnLocode location,
			final VoyageNumber voyage) {
		Validate.notNull(type, "Handling event type is required");
		Validate.notNull(location, "Location is required");

		this.type = type;
		this.location = location;
		this.voyage = voyage;
	}

	public HandlingType type() {
		return type;
	}

	public UnLocode location() {
		return location;
	}

	public VoyageNumber voyage() {
		return voyage;
	}

}
