// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

package issuetoken.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import messaging.Event;
import messaging.MessageQueue;

import javax.ws.rs.core.GenericType;

public class TokenRestService {

	private MessageQueue queue;
	private Map<UUID, CompletableFuture<Object>> completableFutures = new HashMap<>();

	public TokenRestService(MessageQueue q) {
		queue = q;
		queue.addHandler("TokensIssued", this::handleTokensIssued);
	}

	public List<UUID> issue(UUID customerId, int amount) {
		UUID correlationId = UUID.randomUUID();
		CompletableFuture<Object> issuedTokens = new CompletableFuture<>();
		Event event = new Event(correlationId,"TokensRequested", new Object[] { customerId,amount });
		completableFutures.put(correlationId, issuedTokens);
		queue.publish(event);
		return (List<UUID>) issuedTokens.join();
	}

	public void handleTokensIssued(Event e) {
		UUID correlationId = e.getCorrelationId();
		var gson = new Gson();
		var tokens = e.getArgument(0, String.class);
		List<UUID> tokenList = gson.fromJson(tokens,new GenericType<List<UUID>>(){}.getType());
		System.out.println(tokenList.toString());
		CompletableFuture<Object> issuedTokens = completableFutures.get(correlationId);
		issuedTokens.complete(tokenList);
	}
}

