package handling;

public interface TransactionEventType {
	final String TRANSACTION_REQUESTED = "TransactionRequested";
	final String CUSTOMER_REPORT_REQUESTED = "CustomerReportRequested";
	final String MERCHANT_REPORT_REQUESTED = "MerchantReportRequested";
	final String ADMIN_REPORT_REQUESTED = "AdminReportRequested";
	final String TRANSACTION_REQUEST_SUCCESFULL = "TransactionRequestSuccesfull";
	final String TRANSACTION_REQUEST_INVALID = "TransactionRequestInvalid";
	final String REPORT_RESPONSE = "ReportResponse";
	final String REPORT_REQUEST_INVALID = "ReportRequestInvalid";
	
}
