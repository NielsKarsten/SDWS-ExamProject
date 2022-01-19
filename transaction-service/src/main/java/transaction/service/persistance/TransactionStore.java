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
    	System.out.println("getInstance invoked");
        if (instance == null)
            instance = new TransactionStore();

        return instance;
    }

    public void addTransaction(Transaction transaction) {
    	System.out.println("addTransaction invoked");
        transactions.add(transaction);
    }

    public List<Transaction> getAllTransactions() {
    	System.out.println("getAllTransactions invoked");
        return transactions;
    }
    
    public List<Transaction> getCustomerTransactions(UUID customerId){
    	System.out.println("getCustomerTransactions invoked");
    	List<Transaction> customerTransactions = new ArrayList<Transaction>();
    	System.out.println("Size of our transaction list: " + transactions.size());
    	for (Transaction transaction : transactions) {
    		System.out.println("One more round about in the for loop :D ");
    		System.out.println(transaction.toString());
    		if (transaction.getCustomer().equals(customerId))
    		{
    			System.out.println("Found customer transaction");
				customerTransactions.add(transaction);
    		}
    	}
    	System.out.println("Returning a list of customer transaction: " + customerTransactions.toString());
    	return customerTransactions;
    }
    
    public List<Transaction> getMerchantTransactions(UUID merchantId){
    	System.out.println("getMerchantTransactions invoked");
    	List<Transaction> customerTransactions = new ArrayList<Transaction>();
    	for (Transaction transaction : transactions) {
    		if (transaction.getMerchant().equals(merchantId)) {
    			Transaction tmpTransaction = new Transaction(transaction.getMerchant(), null, transaction.getAmount(), transaction.getDescription(), transaction.getToken());
    			tmpTransaction.setCustomer(null);
				customerTransactions.add(tmpTransaction);    			
    		}
    	}
    	return customerTransactions;
    }
}
