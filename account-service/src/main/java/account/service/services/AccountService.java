package account.service.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import account.service.models.User;
import messaging.Event;
import messaging.MessageQueue;

public class AccountService {

	private MessageQueue queue;
	private HashMap<UUID, User> users;
	protected Map<UUID, CompletableFuture<Object>> completableFutures = new HashMap<>();

	// test
	public HashMap<UUID, User> getUsers() {
		return users;
	}

	public AccountService(MessageQueue q) {
		users = new HashMap<UUID, User>();
		queue = q;
		queue.addHandler("AccountRegistrationRequested", this::handleUserAccountRegistration);
		queue.addHandler("UserAccountInfoRequested", this::handleUserAccountInfoRequested);
		queue.addHandler("AccountClosedRequested", this::handleUserAccountClosedRequested);
		queue.addHandler("VerifyUserAccountExistsRequest", this::handleVerifyUserAccountExistsRequest);
		queue.addHandler("ClosedUserAccountTokensRetired", this::handleRetireUserAccountTokensResponse);
		queue.addHandler("AccountClosedRetireTokenRequestInvalid", this::handleRetireUserAccountTokensResponse);
	}

	private void publishNewEvent(Event e, String topic, Object object) {
		UUID correlationId = e.getCorrelationId();
		Event event = new Event(correlationId, topic, new Object[] { object });
		queue.publish(event);
	}

	public void handleUserAccountRegistration(Event e) {
		User user = e.getArgument(0, User.class);
		UUID userId = user.assignUserId();
		users.put(userId, user);
		publishNewEvent(e, "UserAccountRegistered", userId);
	}

	public void handleUserAccountInfoRequested(Event e) {
		try {
			UUID userId = e.getArgument(0, UUID.class);
			String userAccountId = users.get(userId).getAccountId();
			publishNewEvent(e, "UserAccountInfoResponse", userAccountId);
		} catch (NullPointerException ex) {
			publishNewEvent(e, "UserAccountInvalid", false);
		}
	}

	public void handleUserAccountClosedRequested(Event e) {
		try {
			UUID userId = e.getArgument(0, UUID.class);
			boolean success = users.remove(userId) != null && retireUserAccountToken(e, userId);
			if (success) {
				publishNewEvent(e, "AccountClosedResponse", success);
			} else {
				publishNewEvent(e, "UserAccountInvalid", success);
			}
		} catch (Exception exception) {
			publishNewEvent(e, "UserAccountInvalid", false);
		}
	}

	private boolean retireUserAccountToken(Event e, UUID userId) throws Exception {
		UUID correlationId = e.getCorrelationId();
		Event event = new Event(correlationId, "RetireUserAccountTokensRequest", new Object[] {userId});			
		CompletableFuture<Object> tokensRetired = new CompletableFuture<>();
		completableFutures.put(correlationId, tokensRetired);
		this.publishNewEvent(event, "RetireUserAccountTokensRequest", userId);
		return (boolean) completableFutures.get(correlationId).join();
	}
	
	public void handleRetireUserAccountTokensResponse(Event event) {
		UUID correlationId = event.getCorrelationId();
		boolean status =  event.getArgument(0, boolean.class);
		completableFutures.get(correlationId).complete(status);
	}
	
	public void handleRetireUserAccountTokensError(Event event) {
		UUID correlationId = event.getCorrelationId();
		Exception exception =  event.getArgument(0, Exception.class);
		completableFutures.get(correlationId).completeExceptionally(exception);
	}
	
	public void handleVerifyUserAccountExistsRequest(Event e) {
		try {
			UUID userId = e.getArgument(0, UUID.class);
			String userAccountId = users.get(userId).getAccountId();
			boolean accountIdExists = userAccountId != null;			
			
			publishNewEvent(e, "VerifyUserAccountExistsResponse", accountIdExists);
		} catch (NullPointerException ex) {
			publishNewEvent(e, "UserAccountInvalid", false);
		}
	}
}
