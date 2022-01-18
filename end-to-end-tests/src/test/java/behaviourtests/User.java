package behaviourtests;

import java.io.Serializable;
import java.util.UUID;

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