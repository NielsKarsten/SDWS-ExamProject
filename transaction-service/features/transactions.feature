Feature: Transaction Service
  Scenario: a payment is successful
    Given a merchant with merchant id "id1" and a account with balance of 1000
    And a customer with a valid token
    And an amount of 100
    When the transactions is initiated
    Then the transaction is successful

  Scenario: a payment can not not be initiated due to invalid token
    Given a merchant with merchant id "id1" and a account with balance of 1000
    And a customer with a invalid token
    And an amount of 100
    When the transactions is initiated
    Then the transaction is unsuccessful

  Scenario: a payment can not be initiated due to unknown merchant
    Given a merchant with no id
    And a customer with a valid token
    And an amount of 100
    When the transaction is initiated
    Then the transaction is successful

  Scenario: a payment can not be completed due to insufficient funds
    Given a merchant with merchant id "id1" and a account with balance of 50
    And a customer with a invalid token
    And an amount of 100
    When the transactions is initiated
    Then the transaction is unsuccessful and a error message "Insufficient Funds" is returned