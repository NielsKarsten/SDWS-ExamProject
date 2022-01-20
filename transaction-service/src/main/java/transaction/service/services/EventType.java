package transaction.service.services;

public interface EventType {
	
	String USER_ACCOUNT_REGISTRATION = "UserAccountRegistration";
	String ACCOUNT_REGISTRATION_REQUESTED = "AccountRegistrationRequested";
	String USER_ACCOUNT_INFO_REQUESTED = "UserAccountInfoRequested";
	String USER_ACCOUNT_INFO_RESPONSE = "UserAccountInfoResponse";
	String USER_ACCOUNT_INVALID = "UserAccountInvalid";
	String USER_ACCOUNT_ACCOUNT_REGISTERED = "UserAccountRegistered";
	String ACCOUNT_CLOSED_REQUESTED = "AccountClosedRequested";
	String ACCOUNT_CLOSED_RESPONSE = "AccountClosedResponse";
	String VERIFY_USER_ACCOUNT_EXISTS_REQUEST = "VerifyUserAccountExistsRequest";
	String VERIFY_USER_ACCOUNT_EXISTS_RESPONSE = "VerifyUserAccountExistsResponse";
	String TOKENS_REQUESTED = "TokensRequested";
	String TOKEN_TO_CUSTOMER_ID_REQUESTED = "TokenToCustomerIdRequested";
	String TOKEN_TO_CUSTOMER_ID_RESPONSE = "TokenToCustomerIdResponse";
	String TOKEN_TO_CUSTOMER_ID_RESPONSE_INVALID = "TokenToCustomerIdResponseInvalid";
	String TOKEN_REQUEST_INVALID = "TokenRequestInvalid";
	String TOKENS_ISSUED = "TokensIssued";
}
