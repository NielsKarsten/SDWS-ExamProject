package services;

import java.util.UUID;

import accountregistration.service.AccountRegistrationService;
import accountregistration.service.User;
import messaging.Event;

public class EventConstruction {
	private AccountRegistrationService accountRegistrationService;
	private User user;
	private UUID userId;

	public EventConstruction(AccountRegistrationService accountRegistrationService) {
		this.accountRegistrationService = accountRegistrationService;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Object getEventObject(String eventName) {
		Object obj = null;
		switch (eventName) {
			case "AccountRegistrationRequested":
				obj = user;
				break;
			case "UserAccountRegistered":
				userId = UUID.randomUUID();
				obj = userId;
				break;
			case "UserAccountInfoRequested":
				obj = userId;
				break;
			case "UserAccountInfoResponse":
				obj = user.getAccountId();
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
			case "UserAccountRegistered":
				accountRegistrationService.handleUserAccountAssigned(event);
				break;
			default:
				System.out.println("No event handler found for " + eventName);
				break;
		}
	}
}
