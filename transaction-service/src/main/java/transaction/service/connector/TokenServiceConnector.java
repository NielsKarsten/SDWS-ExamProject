package transaction.service.connector;

import messaging.MessageQueue;
import java.util.UUID;
import handling.GenericHandler;
import handling.TokenEventType;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Krikholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Thomas Rathsach Strange
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
