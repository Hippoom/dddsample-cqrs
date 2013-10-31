package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "t_cargo_detail")
@Getter
@Setter
@EqualsAndHashCode(of = "trackingId")
@ToString
public class CargoDto {
	@Id
	@Column(name = "tracking_id")
	private String trackingId;

	@Column(name = "origin_unlocode")
	private String originUnlocode;

	@Column(name = "destination_unlocode")
	private String destinationUnlocode;

	@Temporal(value = TemporalType.DATE)
	@Column(name = "arrival_deadline")
	private Date arrivalDeadline;
}
