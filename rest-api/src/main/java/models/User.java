package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Main: Simon Pontoppidan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
	
}

