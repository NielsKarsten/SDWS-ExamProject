package adapters;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import handling.GenericHandler;
import models.*;
import messaging.Event;
import messaging.MessageQueue;

/**
 * @authors Thomas Rathsach Strange (s153390),
 *          Simon Pontoppidan (s144213),
 *          Niels Karsten Bisgaard-Bohr (s202745)
 */

public class AccountRestService extends GenericHandler {

	public AccountRestService(MessageQueue q) {
		super(q);
		addHandler("UserAccountRegistered", this::genericHandler);
		addHandler("UserAccountInfoResponse", this::genericHandler);
		addHandler("UserAccountInvalid", this::genericErrorHandler);
		addHandler("AccountClosedResponse", this::genericHandler);
		addHandler("VerifyUserAccountExistsResponse", this::genericHandler);
	}

	public UUID registerAsyncUserAccount(User user) {
		if (user.getFirstName() == null || user.getLastName() == null || user.getAccountId() == null)
			throw new NullPointerException("Error - Missing information about user");
		return (UUID) buildCompletableFutureEvent(user, "AccountRegistrationRequested");
	}

	public String requestAsyncUserAccountInfo(UUID userId) {
		return (String) buildCompletableFutureEvent(userId, "UserAccountInfoRequested");
	}

	public Boolean requestAsyncUserAccountDeletion(UUID userId) {
		return (Boolean) buildCompletableFutureEvent(userId, "AccountClosedRequested");
	}
	
	public void handleSuccess(Event e) {
		genericHandler(e);
	}
	
	public void handleError(Event e) {
		genericErrorHandler(e);
	}
}