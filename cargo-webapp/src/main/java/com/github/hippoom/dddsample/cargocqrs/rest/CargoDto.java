package com.github.hippoom.dddsample.cargocqrs.rest;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "t_cargo_detail")
@DynamicUpdate
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

	@Column(name = "routing_status")
	private String routingStatus;

	@Column(name = "transport_status")
	private String transportStatus;

	@Temporal(value = TemporalType.DATE)
	@Column(name = "eta")
	private Date eta;

	@Column(name = "ne_ha_type")
	private String nextExpectedHandlingActivityType;

	@Column(name = "ne_ha_location")
	private String nextExpectedHandlingActivityLocation;

	@Column(name = "ne_ha_voyage")
	private String nextExpectedHandlingActivityVoyageNumber;

	@Column(name = "last_known_location")
	private String lastKnownLocation;

	@ElementCollection
	@CollectionTable(name = "t_cargo_leg_detail", joinColumns = @JoinColumn(name = "tracking_id"))
	@OrderBy("index")
	private List<LegDto> legs;

}
