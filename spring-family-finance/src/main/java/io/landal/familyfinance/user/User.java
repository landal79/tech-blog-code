package io.landal.familyfinance.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class User {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String surname;

	private String username;

	private String password;

	protected User() {
	}

	public User(Long id) {
		super();
		this.id = id;
	}



}
