package services;

import messaging.implementations.RabbitMqQueue; 

public class ServicesFactory {
	static RabbitMqQueue messageQueue = null;
	static TokenRestService tokenservice = null;
	static AccountRestService accountService = null;
	static TransactionRestService transactionService = null;

	public ServicesFactory() {
		messageQueue = new RabbitMqQueue("rabbitMq");
	}
	
	
	public TokenRestService getTokenService() {
		if (tokenservice != null)
			return tokenservice;

		tokenservice = new TokenRestService(messageQueue);
		return tokenservice;
	}
	
	public AccountRestService getAccountService() {
		if (accountService != null) 
			return accountService;

		accountService = new AccountRestService(messageQueue);
		return accountService;
	}
	
	public TransactionRestService getTransactionService() {
		if (transactionService != null) 
			return transactionService;

		transactionService = new TransactionRestService(messageQueue);
		return transactionService;
	}	
}