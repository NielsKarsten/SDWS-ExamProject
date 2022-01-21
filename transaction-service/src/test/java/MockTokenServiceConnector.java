import messaging.MessageQueue;
import transaction.service.connector.TokenServiceConnector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Gustav Lintrup Kirkholt
 */
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
