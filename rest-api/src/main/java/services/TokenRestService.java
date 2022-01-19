// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

package services;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import messaging.Event;
import messaging.MessageQueue;
import models.TokenRequest;

import javax.ws.rs.core.GenericType;

public class TokenRestService {

	private MessageQueue queue;
	private Map<UUID, CompletableFuture<Object>> completableFutures = new ConcurrentHashMap<>();

	public TokenRestService(MessageQueue q) {
		queue = q;
		queue.addHandler("TokensIssued", this::handleTokensIssued);
		queue.addHandler("invalidTokenAmountRequested", this::handleTokenRequestError);
		queue.addHandler("tooManyExistingTokens", this::handleTokenRequestError);
	}

	public Object issueTokens(TokenRequest tokenRequest) {
		System.out.println("issueTokens invoked");
		UUID correlationId = UUID.randomUUID();
		Event event = new Event(correlationId,"TokensRequested", new Object[] { tokenRequest });
		completableFutures.put(correlationId, new CompletableFuture<>());
		queue.publish(event);
		return completableFutures.get(correlationId).join();
	}

	public void handleTokensIssued(Event e) {
		System.out.println("handleTokensIssued invoked");
		UUID correlationId = e.getCorrelationId();
		List<UUID> tokens = (List<UUID>) e.getArgument(0, Object.class);
		completableFutures.get(correlationId).complete(tokens);
	}
	
	public void handleTokenRequestError(Event e) {
		System.out.println("handleTokenRequestError invoked");
		UUID correlationId = e.getCorrelationId();
		String errorMessage = e.getArgument(0, String.class);
		completableFutures.get(correlationId).complete(errorMessage);
	}
}

