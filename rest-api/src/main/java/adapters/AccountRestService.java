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
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Krikholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Niels Bisgaard-Bohr
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
		return UUID.fromString((String) buildCompletableFutureEvent(user, "AccountRegistrationRequested")) ;
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