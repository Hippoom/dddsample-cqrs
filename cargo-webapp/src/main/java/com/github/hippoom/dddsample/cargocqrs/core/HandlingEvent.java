package com.github.hippoom.dddsample.cargocqrs.core;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A HandlingEvent is used to register the event when, for instance, a cargo is
 * unloaded from a carrier at a some loacation at a given time.
 * <p/>
 * The HandlingEvent's are sent from different Incident Logging Applications
 * some time after the event occured and contain information about the
 * {@link se.citerus.dddsample.domain.model.cargo.TrackingId},
 * {@link se.citerus.dddsample.domain.model.location.Location}, timestamp of the
 * completion of the event, and possibly, if applicable a
 * {@link se.citerus.dddsample.domain.model.voyage.Voyage}.
 * <p/>
 * This class is the only member, and consequently the root, of the
 * HandlingEvent aggregate.
 * <p/>
 * HandlingEvent's could contain information about a {@link Voyage} and if so,
 * the event type must be either {@link Type#LOAD} or {@link Type#UNLOAD}.
 * <p/>
 * All other events must be of {@link Type#RECEIVE}, {@link Type#CLAIM} or
 * {@link Type#CUSTOMS}.
 */
@EqualsAndHashCode
@ToString
public class HandlingEvent {

	private HandlingActivity activity;
	private Date completionTime;
	private Date registrationTime;

	public HandlingEvent(HandlingActivity activity, Date completionTime,
			Date registrationTime) {
		this.activity = activity;
		this.completionTime = completionTime;
		this.registrationTime = registrationTime;
	}

	public HandlingType type() {
		return this.activity.type();
	}

	public VoyageNumber voyage() {
		return activity.voyage();
	}

	public Date completionTime() {
		return new Date(this.completionTime.getTime());
	}

	public Date registrationTime() {
		return new Date(this.registrationTime.getTime());
	}

	public UnLocode location() {
		return this.activity.location();
	}

}
