package issuetoken.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import messaging.Event;
import messaging.MessageQueue;

import javax.ws.rs.core.GenericType;

public class IssueTokenService {

	private MessageQueue queue;
	private CompletableFuture<List<Token>> issuedTokens;

	public IssueTokenService(MessageQueue q) {
		queue = q;
		queue.addHandler("TokensIssued", this::handleTokensIssued);
	}

	public List<Token> issue(String customerId, int amount) {
		issuedTokens = new CompletableFuture<>();
		Event event = new Event("TokensRequested", new Object[] { customerId,amount });
		System.out.println("TEST"+event.getArgument(1,int.class));
		queue.publish(event);
		return issuedTokens.join();
	}

	public void handleTokensIssued(Event e) {
		var gson = new Gson();
		var tokens = e.getArgument(0, String.class);
		List<Token> tokenList = gson.fromJson(tokens,new GenericType<List<Token>>(){}.getType());
		issuedTokens.complete(tokenList);
	}
}
