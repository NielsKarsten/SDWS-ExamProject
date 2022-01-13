# Authors:
# Theodor Guttesen s185121
# Main: Christian Gernsøe s163552

Feature: Token Service
Scenario: Generate tokens
	Given customer id "id1"
	When 5 tokens are requested
	Then 5 tokens are generated

Scenario: Customer has too many tokens
	Given customer id "id1"
	When 5 tokens are requested
	Then 5 tokens are generated

#Scenario: Generate tokens for a new user
#	Given a new customer requests tokens from the service
#	Then the tokens are generated

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