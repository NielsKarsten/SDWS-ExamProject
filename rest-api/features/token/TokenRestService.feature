Feature: Issue token feature

  Scenario: Issue tokens
    Given there is a customer with id "aa4aaa2c-c6ca-d5f5-b8b2-0b5c78ee2cb7"
    When the customer requests 3 tokens
    Then the "TokensRequested" event is sent from issuetoken
    When the "TokensIssued" token event is sent
    Then the customer has received 3 tokens
