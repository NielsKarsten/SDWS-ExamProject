package account.service.services;

public interface EventType {
	
	final String USER_ACCOUNT_REGISTRATION = "UserAccountRegistration";
	final String ACCOUNT_REGISTRATION_REQUESTED = "AccountRegistrationRequested";
	final String USER_ACCOUNT_INFO_REQUESTED = "UserAccountInfoRequested";
	final String USER_ACCOUNT_INFO_RESPONSE = "UserAccountInfoResponse";
	final String USER_ACCOUNT_INVALID = "UserAccountInvalid";
	final String USER_ACCOUNT_ACCOUNT_REGISTERED = "UserAccountRegistered";
	final String ACCOUNT_CLOSED_REQUESTED = "AccountClosedRequested";
	final String ACCOUNT_CLOSED_RESPONSE = "AccountClosedResponse";
	final String VERIFY_USER_ACCOUNT_EXISTS_REQUESTS = "VerifyUserAccountExistsRequest";
	final String VERIFY_USER_ACCOUNT_EXISTS_RESPONSE = "VerifyUserAccountExistsResponse";
	
}
