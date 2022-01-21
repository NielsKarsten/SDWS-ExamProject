package account.service.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import account.service.models.User;
import handling.GenericHandler;
import messaging.Event;
import messaging.MessageQueue;

public class AccountService extends GenericHandler{

	private HashMap<UUID, User> users;

	// test
	public HashMap<UUID, User> getUsers() {
		return users;
	}

	public AccountService(MessageQueue q) {
		super(q);
		users = new HashMap<UUID, User>();
		addHandler(EventType.ACCOUNT_REGISTRATION_REQUESTED, this::handleUserAccountRegistration);
		addHandler(EventType.USER_ACCOUNT_INFO_REQUESTED, this::handleUserAccountInfoRequested);
		addHandler(EventType.ACCOUNT_CLOSED_REQUESTED, this::handleUserAccountClosedRequested);
		addHandler(EventType.VERIFY_USER_ACCOUNT_EXISTS_REQUESTS, this::handleVerifyUserAccountExistsRequest);
		addHandler(EventType.CLOSED_USER_ACCOUNT_TOKENS_RETIRED, this::handleRetireUserAccountTokensResponse);
		addHandler(EventType.ACCOUNT_CLOSED_RETIRE_TOKEN_REQUEST_INVALID, this::handleRetireUserAccountTokensError);
	}

	public void handleUserAccountRegistration(Event e) {
		User user = e.getArgument(0, User.class);
		UUID userId = user.assignUserId();
		users.put(userId, user);
		publishNewEvent(e, EventType.USER_ACCOUNT_REGISTERED, userId);
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
		return (boolean) buildCompletableFutureEvent(correlationId, userId, EventType.RETIRE_USER_ACCOUNT_TOKENS_REQUEST);
	}

	public void handleRetireUserAccountTokensResponse(Event event) {
		genericHandler(event);
	}

	public void handleRetireUserAccountTokensError(Event event) {
		genericErrorHandler(event);
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
