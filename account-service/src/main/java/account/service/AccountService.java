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
	}

	public void handleUserAccountRegistration(Event e) {
		User user = e.getArgument(0, User.class);
		UUID userId = user.assignUserId();
		users.put(userId, user);
		Event event = new Event("UserAccountRegistered", new Object[] { userId });
		queue.publish(event);
	}

	public void handleUserAccountInfoRequested(Event e) {
		UUID userId = e.getArgument(0, UUID.class);
		System.out.println("User id: " + userId);
		String userAccountId = users.get(userId).getAccountId();
		Event event = new Event("UserAccountInfoResponse", new Object[] { userAccountId });
		queue.publish(event);
	}

}
