package handling;

/**
 * Main: Niels Bisgaard-Bohr
 */
public interface TokenEventType {
	final String TOKENS_REQUESTED = "TokensRequested";
	final String TOKEN_TO_CUSTOMER_ID_REQUESTED = "TokenToCustomerIdRequested";
	final String ACCOUNT_CLOSED_REQUESTED = "AccountClosedRequested";
	final String TOKEN_TO_CUSTOMER_ID_RESPONSE = "TokenToCustomerIdResponse";
	final String TOKEN_TO_CUSTOMER_ID_RESPONSE_INVALID = "TokenToCustomerIdResponseInvalid";
	final String TOKEN_REQUEST_INVALID = "TokenRequestInvalid";
	final String TOKENS_ISSUED = "TokensIssued";
	
}