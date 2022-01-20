package transaction.service.persistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import transaction.service.models.Transaction;

/**
 * Pseudo database to keep track of all transactions
 * made in the system. Using the singleton pattern we ensure
 * that the hashmap of transactions is coherent across rest calls,
 * transactions and the like
 *
 * Author: Gustav Utke Kauman (s195396)
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
    			Transaction tmpTransaction = new Transaction(transaction.getMerchant(), null, transaction.getAmount(), transaction.getDescription(), transaction.getToken());
				customerTransactions.add(tmpTransaction);    			
    		}
    	}
    	return customerTransactions;
    }
}
