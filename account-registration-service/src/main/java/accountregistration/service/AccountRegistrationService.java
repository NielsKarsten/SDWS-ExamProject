package accountregistration.service;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.netty.util.concurrent.CompleteFuture;
import messaging.Event;
import messaging.MessageQueue;

public class AccountRegistrationService {

	private MessageQueue queue;
	// private HashMap<UUID, CompletableFuture<UUID>> registeredUsersMap;
	private Map<UUID, CompletableFuture<Object>> completableFutures = new HashMap<UUID, CompletableFuture<Object>>();

	public AccountRegistrationService(MessageQueue q) {
		queue = q;
		queue.addHandler("UserAccountRegistered", this::handleUserAccountAssigned);
		queue.addHandler("UserAccountInfoResponse", this::handleUserAccountInfoResponse);
	}

	public UUID registerAsyncUserAccount(User user) {
		CompletableFuture<Object> userAccountToRegister = new CompletableFuture<Object>();

		UUID correlationId = UUID.randomUUID();
		Event event = new Event(correlationId, "AccountRegistrationRequested", new Object[] { user });

		completableFutures.put(correlationId, userAccountToRegister);
		queue.publish(event);
		return (UUID) userAccountToRegister.join();
	}

	public String requestAsyncUserAccountInfo(UUID userId) {
		CompletableFuture<Object> userAccountInfoToRequest = new CompletableFuture<Object>();

		UUID correlationId = UUID.randomUUID();
		Event event = new Event(correlationId, "UserAccountInfoRequested", new Object[] { userId });

		completableFutures.put(correlationId, userAccountInfoToRequest);
		queue.publish(event);
		return (String) userAccountInfoToRequest.join();
	}

	public void handleUserAccountAssigned(Event e) {
		UUID correlationId = e.getCorrelationId();
		UUID userId = e.getArgument(0, UUID.class);
		CompletableFuture<Object> registeredUserAccount = completableFutures.get(correlationId);
		registeredUserAccount.complete(userId);
	}

	public void handleUserAccountInfoResponse(Event e) {
		UUID correlationId = e.getCorrelationId();
		String userAccountId = e.getArgument(0, String.class);
		CompletableFuture<Object> userAccountInfo = completableFutures.get(correlationId);
		userAccountInfo.complete(userAccountId);
	}
}
