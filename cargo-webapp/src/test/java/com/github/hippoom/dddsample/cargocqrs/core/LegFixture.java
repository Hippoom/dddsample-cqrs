package com.github.hippoom.dddsample.cargocqrs.core;

import org.joda.time.DateTime;

public class LegFixture {

	private VoyageNumber voyageNumber = VoyageNumbers.CM001;
	private UnLocode loadLocation = UnLocodes.CNSHA;
	private UnLocode unloadLocation = UnLocodes.CNPEK;
	private DateTime loadTime = new DateTime();
	private DateTime unloadTime = new DateTime();

	public LegFixture(UnLocode loadLocation, UnLocode unloadLocation) {
		this.loadLocation = loadLocation;
		this.unloadLocation = unloadLocation;
	}

	public LegFixture unloadAt(int year, int monthOfYear, int dateOfMonth) {
		this.loadTime = loadTime.withDate(year, monthOfYear, dateOfMonth)
				.withTimeAtStartOfDay();
		return this;
	}

	public LegFixture voyage(VoyageNumber voyageNumber) {
		this.voyageNumber = voyageNumber;
		return this;
	}

	public Leg build() {
		return new Leg(voyageNumber, loadLocation, unloadLocation,
				loadTime.toDate(), unloadTime.toDate());
	}

}
