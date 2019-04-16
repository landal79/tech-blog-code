package io.landal.test.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Address extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private Long number;
	private String street;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id", nullable = false)
	private Person person;


	public Address() {
	}


	public Long getNumber() {
		return number;
	}


	public Address setNumber(Long number) {
		this.number = number;
		return this;
	}


	public String getStreet() {
		return street;
	}


	public Address setStreet(String street) {
		this.street = street;
		return this;
	}


	public Person getPerson() {
		return person;
	}


	public Address setPerson(Person person) {
		this.person = person;
		return this;
	}



}
