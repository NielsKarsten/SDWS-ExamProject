package tokenmanagement.service;

import java.io.Serializable;
import java.util.UUID;

public class TokenRequest implements Serializable {
    private UUID userId;
    private int amount;

    public TokenRequest(UUID userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }
}
