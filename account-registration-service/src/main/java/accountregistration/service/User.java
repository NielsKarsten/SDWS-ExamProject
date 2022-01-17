package accountregistration.service;

import java.io.Serializable;
import java.util.UUID;

/**
* @authors Thomas Rathsach Strange (s153390), 
* 		   Simon Pontoppidan (s144213), 
* 		   Niels Karsten Bisgaard-Bohr (s202745)
*/

public class User implements Serializable{
	private static final long serialVersionUID = -1483091887852909042L;
	private String firstName, lastName;
	private UUID userId;
	private String accountId;
	
	public User(String firstName, String lastName, String accountId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.accountId = accountId;
	}
	
	public UUID assignUserId() {
		this.userId = UUID.randomUUID();
		return this.userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getAccountId() {
		return accountId;
	}
}