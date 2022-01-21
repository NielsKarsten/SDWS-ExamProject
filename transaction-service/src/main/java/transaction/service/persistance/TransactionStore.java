package transaction.service.persistance;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import transaction.service.models.Transaction;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Gustav Utke Kauman
 */
public class TransactionStore {

    private static TransactionStore instance;
    private final List<Transaction> transactions;

    private TransactionStore() {
        transactions = new ArrayList<>();
    }

    public static TransactionStore getInstance() {
        if (instance == null)
            instance = new TransactionStore();

        return instance;
    }

    public static void reset() {
        instance = null;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }
    
    public List<Transaction> getCustomerTransactions(UUID customerId){
    	List<Transaction> customerTransactions = new ArrayList<Transaction>();
    	for (Transaction transaction : transactions) {
    		if (transaction.getCustomer().equals(customerId))
    		{
				customerTransactions.add(transaction);
    		}
    	}
    	return customerTransactions;
    }
    
    public List<Transaction> getMerchantTransactions(UUID merchantId){
    	List<Transaction> customerTransactions = new ArrayList<Transaction>();
    	for (Transaction transaction : transactions) {
    		if (transaction.getMerchant().equals(merchantId)) {
				customerTransactions.add(new Transaction(transaction.getMerchant(), null, transaction.getAmount(), transaction.getDescription(), transaction.getToken()));
    		}
    	}
    	return customerTransactions;
    }
}
