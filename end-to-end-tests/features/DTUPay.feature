# Authors:
# Theodor Guttesen s185121
# Christian Gernsøe s163552
# Gustav Lintrup Kirkholt s164765
# Niels Bisgaard-Bohr S202745
# Simon 
# Thomas

Feature: DTU Pay feature

Scenario: Successful registered a customer
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Then customer exists

Scenario: Succesfully un-register a customer
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Then customer exists
	When customer account is retired
	Then account does not exist

Scenario: Customer succesfully requests tokens
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens

Scenario: Customer requests too many tokens
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When customer requests 5 tokens
	Then customer has 5 tokens
	And they receive an error message "java.lang.Exception: Error: You can only request tokens when you have less than 2 active tokens"
	
Scenario: Customer requests too many tokens
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	When customer requests 10 tokens
	Then they receive an error message "java.lang.Exception: Error: Invalid token amount - you can only request between 1 and 5 tokens at a time"
	
Scenario: Succesfully register a merchant
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	Then merchant exists

Scenario: Succesfully un-register a merchant
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	Then merchant exists
	When merchant account is retired
	Then account does not exist

Scenario: Succesfully register an admin
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	Then merchant exists

Scenario: Successful payment 100
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100
	Then customer has correct balance
	And merchant has correct balance
	When customer account is retired
	Then account does not exist
	When merchant account is retired
	Then account does not exist

Scenario: Successful payment 400
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 400
	Then customer has correct balance
	And merchant has correct balance
	When customer account is retired
	Then account does not exist
	When merchant account is retired
	Then account does not exist

Scenario: Unsuccesful payment due to invalid customer token
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100 with wrong token
	Then customer has correct balance
	And merchant has correct balance
	And they receive an error message "java.lang.Exception: java.lang.IllegalArgumentException: Invalid token"
	
Scenario: Unsuccesful payment due to retired customer token
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100
	And merchant initiates a transaction for 100 again
	Then merchant has correct balance
	And they receive an error message "java.lang.Exception: java.lang.IllegalArgumentException: Invalid token"
	
Scenario: Unsuccesful payment due to retired customer
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When customer account is retired
	When merchant initiates a transaction for 100
	Then merchant has correct balance
	And they receive an error message "java.lang.Exception: Customer does not exists"
	
Scenario: Unsuccesful payment due to unregistered merchant
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When customer requests 5 tokens
	Then customer has 5 tokens
	When unregistered merchant initiates a transaction for 100
	Then customer has correct balance
	And merchant has correct balance
	And they receive an error message "java.lang.Exception: Merchant does not exists"

Scenario: Customer gets transaction
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100.0
	And customer requests transactions
	Then user gets transaction
	When customer account is retired
	Then account does not exist
	When merchant account is retired
	Then account does not exist

Scenario: Unregistered customer gets transactions
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100.0
	When another customer requests transactions
	Then user gets no transactions
	When customer account is retired
	Then account does not exist
	When merchant account is retired
	Then account does not exist

Scenario: Merchant gets transaction
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100.0
	And merchant requests transactions
	Then user gets transaction
	And merchant cannot identify customer identity
	When customer account is retired
	Then account does not exist
	When merchant account is retired
	Then account does not exist

Scenario: Admin gets transaction
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100.0
	And admin requests transactions
	Then user gets transaction
	When customer account is retired
	Then account does not exist
	When merchant account is retired
	Then account does not exist

Scenario: Admin creates and deletes
	Given an admin "Johnny" "Bravo"
	When admin is being registered
	When admin account is retired
	Then account does not exist
	