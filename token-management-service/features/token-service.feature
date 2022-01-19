# Authors:
# Theodor Guttesen s185121
# Niels t
# Main: Christian Gernsøe s163552
# 
Feature: Token Service
Scenario: new Customer requests tokens
	Given a customer
	When 5 tokens are requested
	Then customer recieved 5 tokens

Scenario: Customer tries to request tokens while having more than 1
	Given a customer
	When 2 tokens are requested
	When 5 tokens are requested causing an exception
	Then exception "Error: You can only request tokens when you have less than 2 active tokens" is returned

Scenario: Customer tries to request 6 tokens
	Given a customer
	And has 1 tokens
	When 6 tokens are requested causing an exception
	Then exception "Error: Invalid token amount - you can only request between 1 and 5 tokens at a time" is returned

Scenario: Customer tries to request 0 tokens
	Given a customer
	And has 1 tokens
	When 0 tokens are requested causing an exception
	Then exception "Error: Invalid token amount - you can only request between 1 and 5 tokens at a time" is returned

Scenario: Find customerId through event that gives token
	Given a customer
	And has 1 tokens
	When the "TokenToCustomerIdRequested" event is received
	Then the "TokenToCustomerIdResponse" event is sent

Scenario: Request tokens event test
	Given a customer
	When the "TokensRequested" event is received
	Then the "TokensIssued" event is sent

