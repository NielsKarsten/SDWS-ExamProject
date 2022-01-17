# Authors:
# Theodor Guttesen s185121
# Main: Christian Gernsøe s163552

Feature: Token Service
Scenario: Generate tokens for customer that does not have tokens
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	When 4 tokens are requested
	Then customer has 4 tokens

Scenario: Generate tokens for customer with one token
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	And has 1 tokens
	When 4 tokens are requested
	Then customer has 5 tokens

Scenario: Customer tries to request tokens while having more than 1
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	And has 2 tokens
	When 5 tokens are requested then customer has too many tokens
	Then exception "Request denied - you can only request tokens when you have 1 token" is returned

Scenario: Customer tries to request 6 tokens
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	And has 1 tokens
	When 6 tokens are requested then token limit exceeds
	Then exception "Request denied - you can only request between one and 5 tokens at a time" is returned

Scenario: Customer tries to request 0 tokens
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	And has 1 tokens
	When 0 tokens are requested then token limit exceeds
	Then exception "Request denied - you can only request between one and 5 tokens at a time" is returned

Scenario: Return customer id from token
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	And has 1 tokens
	When get customer id from token
	When get customer id from token then invalid token
	Then exception "Request denied - invalid token" is returned

Scenario: Return customer id from archived token
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	And has 1 tokens
	When get customer id from token
	Then customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7" is returned

Scenario: Consume token from customer
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	And has 1 tokens
	When customer consumes token with expected
	Then customer has 0 tokens

Scenario: Consume token from customer with 0 tokens
	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
	And has 1 tokens
	When customer consumes token with expected
	When customer consumes token
	Then customer has 0 tokens
	And exception "Customer does not have any tokens" is returned

#Scenario: Find customerId through event that gives token
#	Given customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
#	And has 1 tokens
#	When the "TokenToCustomerIdRequested" event is received
#	And get customer id from token
#	Then customer id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7" is returned

