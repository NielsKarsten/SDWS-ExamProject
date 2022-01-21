package account;

import java.util.UUID;

import adapters.AccountRestService;
import handling.AccountEventType;
import messaging.Event;
import models.*;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Krikholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Simon Pontoppidan
 */

public class EventConstruction {
	
	private AccountRestService service;
	private User user;
	private UUID userId;
	

	public EventConstruction(AccountRestService service) {
		this.service = service;
		this.userId = UUID.randomUUID();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		user.assignUserId();
		this.userId = user.getUserId();
	}

	public UUID getUserId() {
		return user.getUserId();
	}

	public Object getEventObject(String eventName) {
		Object obj = null;
		switch (eventName) {
			case AccountEventType.ACCOUNT_REGISTRATION_REQUESTED:
				obj = user;
				break;
			case AccountEventType.USER_ACCOUNT_REGISTERED:
				obj = userId;
				break;
			case AccountEventType.USER_ACCOUNT_INFO_REQUESTED:
				obj = userId;
				break;
			case AccountEventType.USER_ACCOUNT_INFO_RESPONSE:
				obj = user.getAccountId();
				break;
			case AccountEventType.ACCOUNT_CLOSED_REQUESTED:
				obj = userId;
				break;
			case AccountEventType.ACCOUNT_CLOSED_RESPONSE:
				obj = true;
				break;
			default:
				System.out.println("No event object found for " + eventName);
				obj = null;
				break;
		}
		return obj;
	}

	public void handleEventReceived(String eventName, UUID correlationID) {
		Object eventObject = getEventObject(eventName);
		Event event = new Event(correlationID, eventName, new Object[] { eventObject });
		switch (eventName) {
			case AccountEventType.USER_ACCOUNT_REGISTERED:
				service.genericHandler(event);
				break;
			case AccountEventType.USER_ACCOUNT_INFO_RESPONSE:
				service.genericHandler(event);
				break;
			case AccountEventType.ACCOUNT_CLOSED_RESPONSE:
				service.genericHandler(event);
				break;
			default:
				System.out.println("No event handler found for " + eventName);
				break;
		}
	}
}