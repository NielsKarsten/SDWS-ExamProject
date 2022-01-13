package accountregistration.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import messaging.Event;
import messaging.MessageQueue;

public class AccountRegistrationService {

	private MessageQueue queue;
	private CompletableFuture<UUID> registeredUsers;

	public AccountRegistrationService(MessageQueue q) {
		queue = q;
		queue.addHandler("UserAccountRegistered", this::handleUserAccountAssigned);
	}

	public UUID registerAsyncUserAccount(User user) {
		registeredUsers = new CompletableFuture<>();
		Event event = new Event("AccountRegistrationRequested", new Object[] { user });
		queue.publish(event);
		return registeredUsers.join();
	}

	public void handleUserAccountAssigned(Event e) {
		UUID user = e.getArgument(0, UUID.class);
		registeredUsers.complete(user);
	}
}
