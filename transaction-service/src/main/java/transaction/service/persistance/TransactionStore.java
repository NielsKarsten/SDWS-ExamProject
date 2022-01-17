package transaction.service.persistance;

import java.util.HashMap;
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
    private final HashMap<UUID, Transaction> transactions;

    private TransactionStore() {
        transactions = new HashMap<>();
    }

    public static TransactionStore getInstance() {
        if (instance == null)
            instance = new TransactionStore();

        return instance;
    }

    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getCustomer(), transaction);
    }

    public HashMap<UUID, Transaction> getTransactions() {
        return transactions;
    }
}
