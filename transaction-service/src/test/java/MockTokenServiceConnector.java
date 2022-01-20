import messaging.MessageQueue;
import transaction.service.connector.TokenServiceConnector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MockTokenServiceConnector extends TokenServiceConnector {

    private final Map<UUID, UUID> tokens = new HashMap<>();

    public MockTokenServiceConnector(MessageQueue q) {
        super(q);
    }

    public void addToken(UUID userId, UUID token) {
        tokens.put(token, userId);
    }

    @Override
    public UUID getUserIdFromToken(UUID token) {
        return tokens.get(token);
    }
}
