# Authors
# Christian Gernsøe - S163552
# Gustav Utke Kauman - S195396
# Gustav Lintrup Kirkholt - s164765
# Niels Bisgaard-Bohr - S202745
# Simon Pontoppidan - S144213
# Theodor Peter Guttesen - S185121
# Thomas Rathsach Strange - S153390
# 
# Main: Theodor Peter Guttensen
#
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
