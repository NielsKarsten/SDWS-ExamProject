Feature: DTU Pay feature

Scenario: Successful payment
	Given a customer "Johnny" "Bravo" with bank account "1337"
	When customer is being registered
	Given a merchant "Bravo" "Johnny" with bank account "1111"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100
	Then customer has balance 0
	And merchant has balance 100
	When account with id "1337" is retired
	And account with id "1111" is retired
	Then account with id "1337" does not exist
	And account with id "1337" does not exist

#customerAccount = bank.createAccount Name, CPR, balance
#merchantAccount = bank.createAccount Name, CPR, balance
#cid = customerAPI.register Name, customerAccount, ...
#this will register the customer via the customer port of DTU Pay
#mid = merchantAPI.register Name, merchantAccount, ...
#this will register the merchant via the merchant port of DTU Pay
#tokens = customerAPI.getTokens 5, cid
#this will get 5 tokens via the customer port of DTU Pay
#token = select a token from tokens
#merchantAPI.pay token, mid, amount
#this will execute the payment via the merchant port of DTU Pay
#and in DTU Pay, this will cause a call the money transfer service
#of the bank
#customerBalance = bank.getBalance customer account
#merchantBalance = bank.getBalance merchant account
#check that customerBalance and merchantBlance are correct
#bank.retire customer account
#bank.retire merchant account
