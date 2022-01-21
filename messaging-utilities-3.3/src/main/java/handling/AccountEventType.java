package handling;

/**
 * Main: Thomas Rathsach Strange
 */
public interface AccountEventType {	
	final String ACCOUNT_REGISTRATION_REQUESTED = "AccountRegistrationRequested";
	final String USER_ACCOUNT_INFO_REQUESTED = "UserAccountInfoRequested";
	final String USER_ACCOUNT_INFO_RESPONSE = "UserAccountInfoResponse";
	final String USER_ACCOUNT_INVALID = "UserAccountInvalid";
	final String USER_ACCOUNT_REGISTERED = "UserAccountRegistered";
	final String ACCOUNT_CLOSED_REQUESTED = "AccountClosedRequested";
	final String ACCOUNT_CLOSED_RESPONSE = "AccountClosedResponse";
	final String VERIFY_USER_ACCOUNT_EXISTS_REQUESTS = "VerifyUserAccountExistsRequest";
	final String VERIFY_USER_ACCOUNT_EXISTS_RESPONSE = "VerifyUserAccountExistsResponse";
	final String CLOSED_USER_ACCOUNT_TOKENS_RETIRED = "ClosedUserAccountTokensRetired";
	final String ACCOUNT_CLOSED_RETIRE_TOKEN_REQUEST_INVALID = "AccountClosedRetireTokenRequestInvalid";
	final String RETIRE_USER_ACCOUNT_TOKENS_REQUEST = "RetireUserAccountTokensRequest";
}