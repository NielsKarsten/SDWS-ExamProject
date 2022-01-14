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
		queue.addHandler("AccountClosedResponse", this::handleUserAccountClosedResponse);
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

	public Boolean requestAsyncUserAccountDeletion(UUID userId) {
		CompletableFuture<Object> userAccountToDelete = new CompletableFuture<Object>();

		UUID correlationId = UUID.randomUUID();
		Event event = new Event(correlationId, "AccountClosedRequested", new Object[] { userId });

		completableFutures.put(correlationId, userAccountToDelete);
		queue.publish(event);
		return (Boolean) userAccountToDelete.join();
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

	public void handleUserAccountClosedResponse(Event e) {
		UUID correlationId = e.getCorrelationId();
		Boolean userAccountDeletedResponse = e.getArgument(0, Boolean.class);
		CompletableFuture<Object> userAccountDeleted = completableFutures.get(correlationId);
		userAccountDeleted.complete(userAccountDeletedResponse);
	}
}
