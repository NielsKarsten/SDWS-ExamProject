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
public class ServicesFactory {
	static RabbitMqQueue messageQueue = null;
	static TokenRestAdapter tokenservice = null;
	static AccountRestAdapter accountService = null;
	static TransactionRestAdapter transactionService = null;

	public ServicesFactory() {
		messageQueue = new RabbitMqQueue("rabbitMq");
	}
	
	
	public TokenRestAdapter getTokenService() {
		if (tokenservice != null)
			return tokenservice;

		tokenservice = new TokenRestAdapter(messageQueue);
		return tokenservice;
	}
	
	public AccountRestAdapter getAccountService() {
		if (accountService != null) 
			return accountService;

		accountService = new AccountRestAdapter(messageQueue);
		return accountService;
	}
	
	public TransactionRestAdapter getTransactionService() {
		if (transactionService != null) 
			return transactionService;

		transactionService = new TransactionRestAdapter(messageQueue);
		return transactionService;
	}	
}