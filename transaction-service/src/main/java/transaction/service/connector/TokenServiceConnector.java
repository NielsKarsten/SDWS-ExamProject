package transaction.service.connector;

import messaging.MessageQueue;
import java.util.UUID;
import handling.GenericHandler;
import handling.TokenEventType;

/**
 * Connector class to handle communication with the token service
 * Author: Gustav Utke Kauman (s195396), Gustav Lintrup Kirkholt, Niels
 * Bisgaard-Bohr (S202745)
 */
public class TokenServiceConnector extends GenericHandler implements  TokenEventType{
    public TokenServiceConnector(MessageQueue q) {
    	super(q);
        addHandler(TOKEN_TO_CUSTOMER_ID_RESPONSE, this::genericHandler);
        addHandler(TOKEN_TO_CUSTOMER_ID_RESPONSE_INVALID, this::genericErrorHandler);
    }

    public UUID getUserIdFromToken(UUID token) throws Exception {
        return UUID.fromString((String) buildCompletableFutureEvent(token, TOKEN_TO_CUSTOMER_ID_REQUESTED));
    }
}
