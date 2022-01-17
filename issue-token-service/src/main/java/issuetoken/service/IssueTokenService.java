package issuetoken.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import messaging.Event;
import messaging.MessageQueue;

import javax.ws.rs.core.GenericType;

public class IssueTokenService {

	private MessageQueue queue;
	private Map<UUID, CompletableFuture<Object>> completableFutures = new HashMap<>();

	public IssueTokenService(MessageQueue q) {
		queue = q;
		queue.addHandler("TokensIssued", this::handleTokensIssued);
	}

	public List<Token> issue(String customerId, int amount) {
		UUID correlationId = UUID.randomUUID();
		CompletableFuture<Object> issuedTokens = new CompletableFuture<>();
		Event event = new Event(correlationId,"TokensRequested", new Object[] { customerId,amount });
		completableFutures.put(correlationId, issuedTokens);
		queue.publish(event);
		return (List<Token>) issuedTokens.join();
	}

	public void handleTokensIssued(Event e) {
		UUID correlationId = e.getCorrelationId();
		var gson = new Gson();
		var tokens = e.getArgument(0, String.class);
		List<Token> tokenList = gson.fromJson(tokens,new GenericType<List<Token>>(){}.getType());
		System.out.println(tokenList.toString());
		CompletableFuture<Object> issuedTokens = completableFutures.get(correlationId);
		issuedTokens.complete(tokenList);
	}
}

