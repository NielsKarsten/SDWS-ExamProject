package account.service.services;

import java.util.HashMap;
import java.util.UUID;

import account.service.models.User;
import messaging.Event;
import messaging.MessageQueue;

public class AccountService {

	private MessageQueue queue;
	private HashMap<UUID, User> users;

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
			boolean success = users.remove(userId) != null;
			if (success) {
				publishNewEvent(e, EventType.ACCOUNT_CLOSED_RESPONSE, success);
			} else {
				publishNewEvent(e, EventType.USER_ACCOUNT_INVALID, success);
			}
		} catch (Exception exception) {
			publishNewEvent(e, EventType.USER_ACCOUNT_INVALID, false);
		}
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
