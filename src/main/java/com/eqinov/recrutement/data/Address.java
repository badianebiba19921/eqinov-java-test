package com.eqinov.recrutement.data;

import javax.persistence.Embeddable;

/**
 * @author SIMONG - EQINOV
 * @since 27 janv. 2020
 *
 */
@Embeddable
public class Address {
	private String street;
	
	private String postCode;
	
	private String city;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public String getFullAddress() {
		return String.format("%s , %s %s", getStreet(), getPostCode(), getCity());
	}
	
}
