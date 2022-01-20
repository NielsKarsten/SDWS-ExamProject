package services;

import java.util.UUID;

import messaging.Event;
import messaging.MessageQueue;

public class UserHandling extends GenericService{

	public UserHandling(MessageQueue q) {
		super(q);
		this.queue.addHandler("UserAccountExistsResponse", this::handleUserExistsResponse);
	}

	protected boolean verifyUserExists(UUID userId) {
		return (boolean) buildCompletableFutureEvent(userId,"VerifyUserAccountExistsRequest");
	}
	
	protected void handleUserExistsResponse(Event e) {
		genericHandler(e, Boolean.class);
	}
}
