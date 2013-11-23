package com.github.hippoom.dddsample.cargocqrs.core;

import org.joda.time.DateTime;

public class HandlingEventFixture {

	private HandlingType type = HandlingType.RECEIVE;
	private UnLocode location = UnLocodes.CNSHA;
	private VoyageNumber voyageNumber = VoyageNumber.none();
	private DateTime completionTime = new DateTime();

	public HandlingEventFixture(HandlingType type, UnLocode location) {
		this.type = type;
		this.location = location;
	}

	public HandlingEventFixture voyage(VoyageNumber voyageNumber) {
		this.voyageNumber = voyageNumber;
		return this;
	}

	public HandlingEventFixture completeAt(int year, int monthOfYear,
			int dateOfMonth) {
		this.completionTime = completionTime.withDate(year, monthOfYear,
				dateOfMonth).withTimeAtStartOfDay();
		return this;
	}

	public HandlingEvent build() {
		return new HandlingEvent(new HandlingActivity(type, location,
				voyageNumber), completionTime.toDate(), DateTime.now().toDate());
	}

}
