Feature: Issue token feature

  Scenario: Issue tokens
    Given there is a customer with id "id1"
    When the customer requests 3 tokens
    Then the "TokensRequested" event is sent from issuetoken
    When the "TokensIssued" event is sent
    Then the customer has received 3 tokens
