package services;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import models.*;
import messaging.Event;
import messaging.MessageQueue;

/**
 * @authors Thomas Rathsach Strange (s153390),
 *          Simon Pontoppidan (s144213),
 *          Niels Karsten Bisgaard-Bohr (s202745)
 */

public class AccountRestService extends GenericService{

	public AccountRestService(MessageQueue q) {
		super(q);
		queue.addHandler("UserAccountRegistered", this::handleUserAccountAssigned);
		queue.addHandler("UserAccountInfoResponse", this::handleUserAccountInfoResponse);
		queue.addHandler("AccountClosedResponse", this::handleUserAccountClosedResponse);
	}

	public UUID registerAsyncUserAccount(User user) {
		if (user.getFirstName() == null || user.getLastName() == null || user.getAccountId() == null)
			throw new NullPointerException("Error - Missing information about user");
		return (UUID) buildCompletableFutureEvent(user, "AccountRegistrationRequested");
	}

	public void handleUserAccountAssigned(Event e) {
		genericHandler(e, UUID.class);
	}
	
	public String requestAsyncUserAccountInfo(UUID userId) {
		return (String) buildCompletableFutureEvent(userId, "UserAccountInfoRequested");
	}

	public void handleUserAccountInfoResponse(Event e) {
		genericHandler(e, String.class);
	}

	public void handleUserAccountClosedResponse(Event e) {
		genericHandler(e, Boolean.class);
	}

	public Boolean requestAsyncUserAccountDeletion(UUID userId) {
		if (!verifyUserExists(userId))
			throw new IllegalArgumentException("No user exists with userId: " + userId.toString());
		return (Boolean) buildCompletableFutureEvent(userId, "AccountClosedRequested");
	}
	
	
}