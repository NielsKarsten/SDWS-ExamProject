Feature: Issue token feature

  Scenario: Issue tokens
    Given there is a customer with id
    When the customer requests 3 tokens
    Then the "TokensRequested" event is sent from issuetoken
    When the "TokensIssued" token event is sent
    Then the customer has received 3 tokens

  Scenario: Issue tokens with invalid amount
    Given there is a customer with id
    When the customer requests 3 tokens
    Then the "TokensRequested" event is sent from issuetoken
    When the "invalidTokenAmountRequested" token event is sent
    Then the customer has received an error "Error: Invalid token amount - you can only request between 1 and 5 tokens at a time"
    
  Scenario: Issue tokens with invalid amount
    Given there is a customer with id
    When the customer requests 7 tokens
    Then the "TokensRequested" event is sent from issuetoken
    When the "invalidTokenAmountRequested" token event is sent
    Then the customer has received an error "Error: Invalid token amount - you can only request between 1 and 5 tokens at a time"