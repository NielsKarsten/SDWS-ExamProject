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

Scenario: Successful payment
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100
	Then customer has balance 0
	And merchant has balance 100
	When customer account is retired
	Then account does not exist
	When merchant account is retired
	Then account does not exist

Scenario: Customer gets transaction
	Given a customer "Johnny" "Bravo"
	When customer is being registered
	Given a merchant "Bravo" "Johnny"
	When merchant is being registered
	When customer requests 5 tokens
	Then customer has 5 tokens
	When merchant initiates a transaction for 100
	And customer requests transactions
	Then user gets transaction
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
	When merchant initiates a transaction for 100
	And merchant requests transactions
	Then user gets transaction
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
	When merchant initiates a transaction for 100
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