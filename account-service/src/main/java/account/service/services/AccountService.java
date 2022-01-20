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
		queue.addHandler("AccountRegistrationRequested", this::handleUserAccountRegistration);
		queue.addHandler("UserAccountInfoRequested", this::handleUserAccountInfoRequested);
		queue.addHandler("AccountClosedRequested", this::handleUserAccountClosedRequested);
		queue.addHandler("VerifyUserAccountExistsRequest", this::handleVerifyUserAccountExistsRequest);
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
			boolean success = users.remove(userId) != null;
			if (success) {
				publishNewEvent(e, "AccountClosedResponse", success);
			} else {
				publishNewEvent(e, "UserAccountInvalid", success);
			}
		} catch (Exception exception) {
			publishNewEvent(e, "UserAccountInvalid", false);
		}
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
