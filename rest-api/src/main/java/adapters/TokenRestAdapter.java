// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

package adapters;

import java.util.*;
import handling.GenericHandler;
import handling.TokenEventType;
import messaging.MessageQueue;
import models.TokenRequest;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Christian Gernsøe
 */
public class TokenRestAdapter extends GenericHandler implements TokenEventType{
	public TokenRestAdapter(MessageQueue q) {
		super(q);
		addHandler(TOKENS_ISSUED, this::genericHandler);
		addHandler(TOKEN_REQUEST_INVALID, this::genericErrorHandler);
	}

	public List<UUID> issueTokens(TokenRequest tokenRequest) throws Exception {
		return (List<UUID>) buildCompletableFutureEvent(tokenRequest,"TokensRequested");						
	}

}