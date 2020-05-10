package com.eqinov.recrutement.data;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Entit� Jpa repr�sentant un site client physique
 * 
 * @author Guillaume SIMON - EQINOV
 * @since 24 janv. 2020
 *
 */
@Entity
public class Site {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	
	@Embedded
	private Address address;

	@OneToMany(mappedBy = "site")
	private List<DataPoint> consos;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<DataPoint> getConsos() {
		return consos;
	}

	public void setConsos(List<DataPoint> consos) {
		this.consos = consos;
	}

}