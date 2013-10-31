package com.github.hippoom.dddsample.cargocqrs.command;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.github.hippoom.dddsample.cargocqrs.core.UnLocode;

@EqualsAndHashCode
@ToString
@Getter
public class RegisterCargoCommand {
	private final UnLocode originUnLocode;
	private final UnLocode destinationUnLocode;
	private final Date arrivalDeadline;

	public RegisterCargoCommand(UnLocode originUnLocode,
			UnLocode destinationUnLocode, Date arrivalDeadline) {
		this.originUnLocode = originUnLocode;
		this.destinationUnLocode = destinationUnLocode;
		this.arrivalDeadline = arrivalDeadline;
	}

}
