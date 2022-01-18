Feature: Token Manager
Scenario: Add token for new user
	Given a user
	When 5 tokens are generated
	Then user has 5 tokens

Scenario: Generate tokens for customer with one token
	Given a user
	When 1 tokens are generated
	And 4 tokens are generated
	Then user has 5 tokens

Scenario: Verify token owner
	Given a user
	When 1 tokens are generated
	Then is the owner of the tokens

Scenario: Token is removed
	Given a user
	When 1 tokens are generated
	When token is removed
	Then token is succesfully removed
