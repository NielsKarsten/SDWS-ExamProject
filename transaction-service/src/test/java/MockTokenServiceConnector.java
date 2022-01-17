import messaging.MessageQueue;
import transaction.service.connector.TokenServiceConnector;

import java.util.UUID;

public class MockTokenServiceConnector extends TokenServiceConnector {
    public MockTokenServiceConnector(MessageQueue q) {
        super(q);
    }

    @Override
    public UUID getUserIdFromToken(UUID token) {
        return UUID.randomUUID();
    }
}
