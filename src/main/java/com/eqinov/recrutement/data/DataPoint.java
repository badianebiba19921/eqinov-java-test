package com.eqinov.recrutement.data;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entité Jpa listant les points de consommation à pas 10 minutes d'un site client
 * 
 * @author Guillaume SIMON - EQINOV
 * @since 24 janv. 2020
 *
 */
@Entity
@IdClass(DataPointId.class)
public class DataPoint {
	
	@Id
	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;
	
	@Id
	private LocalDateTime time;
	
	private Double value;
	
	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
		
}