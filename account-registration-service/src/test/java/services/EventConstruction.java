package services;

import java.util.UUID;

import accountregistration.service.AccountRegistrationService;
import accountregistration.service.User;
import messaging.Event;

/**
* @authors Thomas Rathsach Strange (s153390), 
* 		   Simon Pontoppidan (s144213), 
* 		   Niels Karsten Bisgaard-Bohr (s202745)
*/

public class EventConstruction {
	
	private AccountRegistrationService accountRegistrationService;
	private User user;
	private UUID userId;
	

	public EventConstruction(AccountRegistrationService accountRegistrationService) {
		this.accountRegistrationService = accountRegistrationService;
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
			case "AccountRegistrationRequested":
				obj = user;
				break;
			case "UserAccountRegistered":
				obj = userId;
				break;
			case "UserAccountInfoRequested":
				obj = userId;
				break;
			case "UserAccountInfoResponse":
				obj = user.getAccountId();
				break;
			case "AccountClosedRequested":
				obj = userId;
				break;
			case "AccountClosedResponse":
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
			case "UserAccountRegistered":
				accountRegistrationService.handleUserAccountAssigned(event);
				break;
			case "UserAccountInfoResponse":
				accountRegistrationService.handleUserAccountInfoResponse(event);
				break;
			case "AccountClosedResponse":
				accountRegistrationService.handleUserAccountClosedResponse(event);
				break;
			default:
				System.out.println("No event handler found for " + eventName);
				break;
		}
	}
}
