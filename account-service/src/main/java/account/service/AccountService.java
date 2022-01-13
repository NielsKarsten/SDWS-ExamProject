package account.service;

import java.util.HashMap;
import java.util.UUID;

import messaging.Event;
import messaging.MessageQueue;

public class AccountService {

	private MessageQueue queue;
	private HashMap<UUID, User> users;

	public HashMap<UUID, User> getUsers() {
		return users;
	}

	public AccountService(MessageQueue q) {
		users = new HashMap<UUID, User>();
		queue = q;
		queue.addHandler("AccountRegistrationRequested", this::handleUserAccountRegistration);
		queue.addHandler("UserAccountInfoRequested", this::handleUserAccountInfoRequested);
		queue.addHandler("AccountClosedRequested", this::handleUserAccountClosedRequested);
	}

	public void handleUserAccountRegistration(Event e) {
		User user = e.getArgument(0, User.class);
		UUID userId = user.assignUserId();
		users.put(userId, user);
		Event event = new Event("UserAccountRegistered", new Object[] { userId });
		queue.publish(event);
	}

	public void handleUserAccountInfoRequested(Event e) {
		Event event;
		try {
			UUID userId = e.getArgument(0, UUID.class);
			String userAccountId = users.get(userId).getAccountId();
			event = new Event("UserAccountInfoResponse", new Object[] { userAccountId });
		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
			event = new Event("UserAccountInfoResponse", new Object[] { null });
		}
		queue.publish(event);
	}

	public void handleUserAccountClosedRequested(Event e) {
		UUID userId = e.getArgument(0, UUID.class);
		boolean success = users.remove(userId) != null;
		Event event = new Event("AccountClosedResponse", new Object[] { success });
		queue.publish(event);
	}

}
