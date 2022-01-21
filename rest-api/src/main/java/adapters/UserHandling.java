package adapters;

import java.util.UUID;

import handling.GenericHandler;
import messaging.MessageQueue;

public class UserHandling extends GenericHandler{

	public UserHandling(MessageQueue q) {
		super(q);
		addHandler("UserAccountExistsResponse", this::genericHandler);
	}

	protected boolean verifyUserExists(UUID userId) {
		return (boolean) buildCompletableFutureEvent(userId,"VerifyUserAccountExistsRequest");
	}
}
