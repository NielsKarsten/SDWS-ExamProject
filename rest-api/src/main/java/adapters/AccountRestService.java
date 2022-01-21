package adapters;

import java.util.UUID;
import handling.AccountEventType;
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

public class AccountRestService extends GenericHandler implements AccountEventType {

	public AccountRestService(MessageQueue q) {
		super(q);
		addHandler(USER_ACCOUNT_REGISTERED, this::genericHandler);
		addHandler(USER_ACCOUNT_INFO_RESPONSE, this::genericHandler);
		addHandler(USER_ACCOUNT_INVALID, this::genericErrorHandler);
		addHandler(ACCOUNT_CLOSED_RESPONSE, this::genericHandler);
		addHandler(VERIFY_USER_ACCOUNT_EXISTS_RESPONSE, this::genericHandler);
	}

	public UUID registerAsyncUserAccount(User user) {
		if (user.getFirstName() == null || user.getLastName() == null || user.getAccountId() == null)
			throw new NullPointerException("Error - Missing information about user");
		return UUID.fromString((String) buildCompletableFutureEvent(user, ACCOUNT_REGISTRATION_REQUESTED)) ;
	}

	public String requestAsyncUserAccountInfo(UUID userId) {
		return (String) buildCompletableFutureEvent(userId, USER_ACCOUNT_INFO_REQUESTED);
	}

	public Boolean requestAsyncUserAccountDeletion(UUID userId) {
		return (Boolean) buildCompletableFutureEvent(userId, ACCOUNT_CLOSED_REQUESTED);
	}
	
	public void handleSuccess(Event e) {
		genericHandler(e);
	}
	
	public void handleError(Event e) {
		genericErrorHandler(e);
	}
}