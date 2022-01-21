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
		queue.addHandler(EventType.ACCOUNT_REGISTRATION_REQUESTED, this::handleUserAccountRegistration);
		queue.addHandler(EventType.USER_ACCOUNT_INFO_REQUESTED, this::handleUserAccountInfoRequested);
		queue.addHandler(EventType.ACCOUNT_CLOSED_REQUESTED, this::handleUserAccountClosedRequested);
		queue.addHandler(EventType.VERIFY_USER_ACCOUNT_EXISTS_REQUESTS, this::handleVerifyUserAccountExistsRequest);
		queue.addHandler("ClosedUserAccountTokensRetired", this::handleRetireUserAccountTokensResponse);
		queue.addHandler("AccountClosedRetireTokenRequestInvalid", this::handleRetireUserAccountTokensError);
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
		publishNewEvent(e, EventType.USER_ACCOUNT_ACCOUNT_REGISTERED, userId);
	}

	public void handleUserAccountInfoRequested(Event e) {
		try {
			UUID userId = e.getArgument(0, UUID.class);
			String userAccountId = users.get(userId).getAccountId();
			publishNewEvent(e, EventType.USER_ACCOUNT_INFO_RESPONSE, userAccountId);
		} catch (NullPointerException ex) {
			publishNewEvent(e, EventType.USER_ACCOUNT_INVALID, false);
		}
	}

	public void handleUserAccountClosedRequested(Event e) {
		try {
			UUID userId = e.getArgument(0, UUID.class);
			if (users.get(userId) == null)
				throw new NullPointerException("No user with ID exists");
			boolean success = retireUserAccountToken(e, userId) && users.remove(userId) != null;
			if (success) {
				publishNewEvent(e, EventType.ACCOUNT_CLOSED_RESPONSE, success);
			} else {
				publishNewEvent(e, EventType.USER_ACCOUNT_INVALID, success);
			}
		} catch (Exception exception) {
			System.out.println("Ending up in exception " + exception.getMessage());
			publishNewEvent(e, EventType.USER_ACCOUNT_INVALID, false);
		}
	}

	private boolean retireUserAccountToken(Event e, UUID userId) throws Exception {
		UUID correlationId = e.getCorrelationId();
		Event event = new Event(correlationId, "RetireUserAccountTokensRequest", new Object[] { userId });
		CompletableFuture<Object> tokensRetired = new CompletableFuture<>();
		completableFutures.put(correlationId, tokensRetired);
		this.publishNewEvent(event, "RetireUserAccountTokensRequest", userId);
		return (boolean) completableFutures.get(correlationId).join();
	}

	public void handleRetireUserAccountTokensResponse(Event event) {
		System.out.println("handleRetireUserAccountTokensResponse");
		UUID correlationId = event.getCorrelationId();
		boolean status = event.getArgument(0, boolean.class);
		completableFutures.get(correlationId).complete(status);
	}

	public void handleRetireUserAccountTokensError(Event event) {
		System.out.println("handleRetireUserAccountTokensError");
		UUID correlationId = event.getCorrelationId();
		Exception exception = event.getArgument(0, Exception.class);
		completableFutures.get(correlationId).completeExceptionally(exception);
	}

	public void handleVerifyUserAccountExistsRequest(Event e) {
		try {
			UUID userId = e.getArgument(0, UUID.class);
			String userAccountId = users.get(userId).getAccountId();
			boolean accountIdExists = userAccountId != null;
			publishNewEvent(e, EventType.VERIFY_USER_ACCOUNT_EXISTS_RESPONSE, accountIdExists);
		} catch (NullPointerException ex) {
			publishNewEvent(e, EventType.USER_ACCOUNT_INVALID, false);
		}
	}
}
