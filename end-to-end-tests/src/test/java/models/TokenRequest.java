package models;

import java.util.UUID;

public class TokenRequest {
    private UUID userId;
    private int amount;

    public TokenRequest(UUID userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
