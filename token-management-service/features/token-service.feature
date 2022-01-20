# Authors:
# Theodor Guttesen s185121
# Niels t
# Main: Christian Gernsøe s163552
# 
Feature: Token Service
Scenario: new Customer requests tokens
	Given a customer
	When 5 tokens are requested
	And the "TokensRequested" event is received
	Then the "TokensIssued" event is sent
	And customer recieved 5 tokens

Scenario: Customer tries to request tokens while having 2 tokens
	Given a customer
	When 2 tokens are requested
	And the "TokensRequested" event is received
	Then the "TokensIssued" event is sent
	When 5 tokens are requested
	And the "TokensRequested" event is received
	Then the "TokenRequestInvalid" event is sent
	And An exception is thrown

Scenario: Customer tries to request 6 tokens
	Given a customer
	When 6 tokens are requested
	And the "TokensRequested" event is received
	Then the "TokenRequestInvalid" event is sent
	And An exception is thrown

Scenario: Customer tries to request 0 tokens
	Given a customer
	When 0 tokens are requested
	And the "TokensRequested" event is received
	Then the "TokenRequestInvalid" event is sent
	And An exception is thrown

Scenario: Find customerId through event that gives token
	Given a customer
	When 1 tokens are requested
	And the "TokensRequested" event is received
	Then the "TokensIssued" event is sent
	When the "TokenToCustomerIdRequested" event is received
	Then the "TokenToCustomerIdResponse" event is sent
	And the id matches the customer