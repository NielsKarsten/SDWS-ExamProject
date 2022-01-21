package handling;

public interface TokenEventType {
	String TOKENS_REQUESTED = "TokensRequested";
	String TOKEN_TO_CUSTOMER_ID_REQUESTED = "TokenToCustomerIdRequested";
	String ACCOUNT_CLOSED_REQUESTED = "AccountClosedRequested";
	String TOKEN_TO_CUSTOMER_ID_RESPONSE = "TokenToCustomerIdResponse";
	String TOKEN_TO_CUSTOMER_ID_RESPONSE_INVALID = "TokenToCustomerIdResponseInvalid";
	String TOKEN_REQUEST_INVALID = "TokenRequestInvalid";
	String TOKENS_ISSUED = "TokensIssued";
}