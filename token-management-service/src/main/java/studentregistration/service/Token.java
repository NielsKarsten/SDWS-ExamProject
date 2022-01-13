// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

package studentregistration.service;

import java.util.UUID;

public class Token {
    private UUID token;

    public Token() {
        this.token = UUID.randomUUID();
    }

    protected UUID getToken() {
        return token;
    }
}
