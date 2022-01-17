import dk.dtu.sdws.group3.connector.TokenServiceConnector;
import messaging.MessageQueue;

import java.util.UUID;

public class MockTokenServiceConnector extends TokenServiceConnector {
    public MockTokenServiceConnector(MessageQueue q) {
        super(q);
    }

    @Override
    public String getUserIdFromToken(UUID token) {
        return UUID.randomUUID().toString();
    }
}
