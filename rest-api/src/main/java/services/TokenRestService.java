// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

package services;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import messaging.Event;
import messaging.MessageQueue;
import models.TokenRequest;

import javax.ws.rs.core.GenericType;

public class TokenRestService {

	private MessageQueue queue;
	private Map<UUID, CompletableFuture<Object>> completableFutures = new HashMap<>();

	public TokenRestService(MessageQueue q) {
		queue = q;
		queue.addHandler("TokensIssued", this::handleTokensIssued);
	}

	public List<UUID> issueTokens(TokenRequest tokenRequest) {
		UUID correlationId = UUID.randomUUID();
		Event event = new Event(correlationId,"TokensRequested", new Object[] { tokenRequest });
		completableFutures.put(correlationId, new CompletableFuture<>());
		queue.publish(event);
		return (List<UUID>) completableFutures.get(correlationId).join();
	}

	public void handleTokensIssued(Event e) {
		UUID correlationId = e.getCorrelationId();
		Gson gson = new Gson();
		String tokens = e.getArgument(0, String.class);
		System.out.println("Token before GSON " + tokens);
		List<UUID> tokenList = gson.fromJson(tokens,new GenericType<List<UUID>>(){}.getType());
		System.out.println(tokenList.toString());
		completableFutures.get(correlationId).complete(tokenList);
//		CompletableFuture<Object> issuedTokens = completableFutures.get(correlationId);
//		issuedTokens.complete(tokenList);
	}
}

