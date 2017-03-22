package swt6.spring.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable

public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String zipCode;
	@Column(nullable = false)
	private String city;
	@Column(nullable = false)
	private String street;

	public Address() {
	}

	public Address(String zipCode, String city, String street) {
		this.zipCode = zipCode;
		this.city = city;
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String toString() {
		return zipCode + " " + city + ", " + street;
	}
}
