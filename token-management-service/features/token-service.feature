# Authors:
# Theodor Guttesen s185121
# Main: Christian Gernsøe s163552

Feature: Token Service
Scenario: Generate tokens for customer that does not have tokens
	Given customer id "id1"
	When 4 tokens are requested
	Then customer has 4 tokens

Scenario: Generate tokens for customer with one token
	Given customer id "id1"
	And has 1 tokens
	When 4 tokens are requested
	Then customer has 5 tokens

Scenario: Customer tries to request tokens while having more than 1
	Given customer id "id1"
	And has 2 tokens
	When 5 tokens are requested then customer has too many tokens
	Then exception "Request denied - you can only request tokens when you have 1 token" is returned

Scenario: Customer tries to request 6 tokens
	Given customer id "id1"
	And has 1 tokens
	When 6 tokens are requested then token limit exceeds
	Then exception "Request denied - you can only request between one and 5 tokens at a time" is returned

Scenario: Customer tries to request 0 tokens
	Given customer id "id1"
	And has 1 tokens
	When 0 tokens are requested then token limit exceeds
	Then exception "Request denied - you can only request between one and 5 tokens at a time" is returned

#Scenario: Return user id from token
#	Given customer id "id1"
#	And has 1 tokens
#	When get customer id from token
#	Then customer id "id1" is returned

#Scenario: Generate tokens for a user
#	Given a customer requests tokens from the service
#	Then the tokens are generated

#Scenario: Get token owner
#	Given a new customer requests tokens from the service
#	When the owner of a token is requested
#	Then the correct owner identified

#Scenario: Get non-existent token owner
#	Given a token with no owner
#	When the owner of a token is requested
#	Then the service returns an error with message "Invalid token"

#Scenario: Generate tokens for a user with more than one active token
#	Given a customer requests tokens from the service
#	When the customer requests tokens from the service
#	Then the tokens are not generated

#Scenario: Validate valid token
#	Given a customer requests tokens from the service
#	When the token is parsed to the service
#	Then the token is validated succesfully

#Scenario: Validate wrong token
#	Given a token with no owner
#	Then the token is validated not succesfully

#Scenario: Consume token
#	Given a customer requests tokens from the service
#	When a token is parsed to the service
#	Then the token is removed from active tokens
#	And the token is added to the list of used tokens

#Scenario: Consume invalid token
#	Given a token with no owner
#	Then the service returns an error with message "Invalid token"