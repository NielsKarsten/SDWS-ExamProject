package adapters;

import messaging.implementations.RabbitMqQueue; 

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
public class AdapterFactory {
	static RabbitMqQueue messageQueue = null;
	static TokenRestAdapter tokenRestAdapter = null;
	static AccountRestAdapter accountRestAdapter = null;
	static TransactionRestAdapter transactionRestAdapter = null;

	public AdapterFactory() {
		messageQueue = new RabbitMqQueue("rabbitMq");
	}
	
	
	public TokenRestAdapter getTokenRestAdapter() {
		if (tokenRestAdapter != null)
			return tokenRestAdapter;

		tokenRestAdapter = new TokenRestAdapter(messageQueue);
		return tokenRestAdapter;
	}
	
	public AccountRestAdapter getAccountRestAdapter() {
		if (accountRestAdapter != null)
			return accountRestAdapter;

		accountRestAdapter = new AccountRestAdapter(messageQueue);
		return accountRestAdapter;
	}
	
	public TransactionRestAdapter getTransactionRestAdapter() {
		if (transactionRestAdapter != null)
			return transactionRestAdapter;

		transactionRestAdapter = new TransactionRestAdapter(messageQueue);
		return transactionRestAdapter;
	}	
}