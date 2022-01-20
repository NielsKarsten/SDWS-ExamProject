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
   