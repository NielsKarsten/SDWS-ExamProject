package account.service.models;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Krikholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Niels Bisgaard-Bohr
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
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
