# Authors:
# Theodor Guttesen s185121
# Christian Gernsøe s163552
# Gustav Lintrup Kirkholt s164765


Feature: DTU Pay feature

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