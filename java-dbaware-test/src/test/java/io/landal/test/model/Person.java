package io.landal.test.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({ @NamedQuery(name = Person.QUERY_DELETE_ALL, query = "delete from Person") })
public class Person extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public static final String QUERY_DELETE_ALL = "Person.deleteAll";

	private String name;
	private String surname;

	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Address> addresses;

	public Person() {
	}

	public Person addAddress(Address address) {
		Objects.requireNonNull(address);

		if (this.addresses == null) {
			this.addresses = new HashSet<>();
		}

		address.setPerson(this);
		this.addresses.add(address);
		return this;

	}

	/////////////////////

	public String getName() {
		return name;
	}

	public Person setName(String name) {
		this.name = name;
		return this;
	}

	public String getSurname() {
		return surname;
	}

	public Person setSurname(String surname) {
		this.surname = surname;
		return this;
	}

}
