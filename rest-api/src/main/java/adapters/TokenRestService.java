// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

package adapters;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import handling.GenericHandler;
import messaging.Event;
import messaging.MessageQueue;
import models.TokenRequest;

import javax.ws.rs.core.GenericType;

public class TokenRestService extends GenericHandler{

	public TokenRestService(MessageQueue q) {
		super(q);
		addHandler("TokensIssued", this::genericHandler);
		addHandler("TokenRequestInvalid", this::genericErrorHandler);
	}

	public List<UUID> issueTokens(TokenRequest tokenRequest) throws Exception {
		return (List<UUID>) buildCompletableFutureEvent(tokenRequest,"TokensRequested");						
	}

}