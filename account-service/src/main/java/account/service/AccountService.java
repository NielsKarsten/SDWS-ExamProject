package account.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import messaging.Event;
import messaging.MessageQueue;

public class AccountService {

	private MessageQueue queue;
	private Map<UUID,User> users;

	public Map<UUID, User> getUsers() {
		return users;
	}

	public AccountService(MessageQueue q) {
		users = new HashMap<UUID,User>();
		queue = q;
		queue.addHandler("AccountRegistrationRequested", this::handleUserAccountRegistration);
	}

	public void handleUserAccountRegistration(Event e) {
		User user = e.getArgument(0, User.class);
		UUID userId = user.assignUserId();
		users.put(userId, user);
		Event event = new Event("UserAccountRegistered", new Object[] { userId });
		queue.publish(event);
	}
	
	
}
