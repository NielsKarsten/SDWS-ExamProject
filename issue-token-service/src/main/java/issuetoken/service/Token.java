// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

package issuetoken.service;

import java.io.Serializable;
import java.util.UUID;

public class Token implements Serializable{
	private static final long serialVersionUID = 9023222981284806610L;

	private UUID token;

	public Token() {
		this.token = UUID.randomUUID();
	}
}
