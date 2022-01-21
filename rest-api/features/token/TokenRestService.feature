# Authors
# Christian Gernsøe - S163552
# Gustav Utke Kauman - S195396
# Gustav Lintrup Krikholt - s164765
# Niels Bisgaard-Bohr - S202745
# Simon Pontoppidan - S144213
# Theodor Peter Guttesen - S185121
# Thomas Rathsach Strange - S153390
# 
# Main: Theodor Peter Guttensen
#
Feature: Issue token feature

  Scenario: Issue tokens
    Given there is a customer with id
    When the customer requests 3 tokens
    Then the "TokensRequested" event is sent from TokenRestService
    When the "TokensIssued" token event is received
    Then the customer has received 3 tokens

  Scenario: Issue tokens with invalid amount
    Given there is a customer with id
    When the customer requests 3 tokens
    Then the "TokensRequested" event is sent from TokenRestService
    When the "TokenRequestInvalid" token event is received
    Then the customer has received an error
   