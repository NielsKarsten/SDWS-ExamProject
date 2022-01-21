package adapters;

import messaging.MessageQueue;
import models.Transaction;
import models.TransactionRequest;
import java.util.List;
import java.util.UUID;
import handling.GenericHandler;
import handling.TransactionEventType;
/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Niels Bisgaard-Bohr
 */
public class TransactionRestAdapter extends GenericHandler implements TransactionEventType{

    public TransactionRestAdapter(MessageQueue q) {
    	super(q);
    	addHandler(TRANSACTION_REQUEST_SUCCESFULL, this::genericHandler);
        addHandler(TRANSACTION_REQUEST_INVALID, this::genericErrorHandler);
        addHandler(REPORT_RESPONSE, this::genericHandler);
        addHandler(REPORT_REQUEST_INVALID, this::genericErrorHandler);
    }

	public String createTransactionRequest(TransactionRequest request) throws Exception {
    	return (String) buildCompletableFutureEvent(request,TRANSACTION_REQUESTED);
    }
    
    public List<Transaction> getAdminTransactions() throws Exception {
    	return (List<Transaction>) buildCompletableFutureEvent(null,ADMIN_REPORT_REQUESTED);
    }

    public List<Transaction> getMerchantTransactions(UUID merchantId) throws Exception {
    	return (List<Transaction>) buildCompletableFutureEvent(merchantId,MERCHANT_REPORT_REQUESTED);
    }

    public List<Transaction> getCustomerTransactions(UUID userId) throws Exception{
    	return (List<Transaction>) buildCompletableFutureEvent(userId,CUSTOMER_REPORT_REQUESTED);
    }
}