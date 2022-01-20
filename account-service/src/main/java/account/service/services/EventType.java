package account.service.services;

public interface EventType {
	
	String USER_ACCOUNT_REGISTRATION = "UserAccountRegistration";
	String ACCOUNT_REGISTRATION_REQUESTED = "AccountRegistrationRequested";
	String USER_ACCOUNT_INFO_REQUESTED = "UserAccountInfoRequested";
	String USER_ACCOUNT_INFO_RESPONSE = "UserAccountInfoResponse";
	String USER_ACCOUNT_INVALID = "UserAccountInvalid";
	String USER_ACCOUNT_ACCOUNT_REGISTERED = "UserAccountRegistered";
	String ACCOUNT_CLOSED_REQUESTED = "AccountClosedRequested";
	String ACCOUNT_CLOSED_RESPONSE = "AccountClosedResponse";
	String VERIFY_USER_ACCOUNT_EXISTS_REQUESTS = "VerifyUserAccountExistsRequest";
	String VERIFY_USER_ACCOUNT_EXISTS_RESPONSE = "VerifyUserAccountExistsResponse";
	
}
