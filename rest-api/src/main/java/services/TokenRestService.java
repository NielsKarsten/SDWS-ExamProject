// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

package services;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import messaging.Event;
import messaging.MessageQueue;
import models.TokenRequest;

import javax.ws.rs.core.GenericType;

public class TokenRestService extends GenericService{

	public TokenRestService(MessageQueue q) {
		super(q);
		addHandler("TokensIssued", this::handleTokensIssued);
		addHandler("TokenRequestInvalid", this::handleTokenRequestError);
	}

	public List<UUID> issueTokens(TokenRequest tokenRequest) throws Exception {
		return (List<UUID>) buildCompletableFutureEvent(tokenRequest,"TokensRequested");						
	}
	
	public void handleTokensIssued(Event e) {
		genericHandler(e,Object.class);
	}
	
	public void handleTokenRequestError(Event e) {
		genericErrorHandler(e);
	}
}