Feature: Transaction Service
  Scenario: a payment is successful
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And an amount of 100
    When the transaction is initiated
    Then the transaction is successful
    And the transaction is saved

  Scenario: receiving a transaction request
    When a "TransactionRequest" event is received
    Then a "TransactionRequestResponse" event is sent
    And the transaction response has status successful

  Scenario: Customer report is requested
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And a list of transactions
    When a "CustomerReportRequested" event is received
    Then a "CustomerReportResponse" event is sent

  Scenario: Merchant report is requested
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And a list of transactions
    When a "MerchantReportRequested" event is received
    Then a "MerchantReportResponse" event is sent
   
  Scenario: Admin report is requested
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And a list of transactions
    When a "AdminReportRequested" event is received
    Then a "AdminReportResponse" event is sent
#
#  Scenario: a payment can not not be initiated due to invalid token
#    Given a merchant with merchant id "id1" and a account with balance of 1000
#    And a customer with a invalid token
#    And an amount of 100
#    When the transactions is initiated
#    Then the transaction is unsuccessful
#
#  Scenario: a payment can not be initiated due to unknown merchant
#    Given a merchant with no id
#    And a customer with a valid token
#    And an amount of 100
#    When the transaction is initiated
#    Then the transaction is successful
#
#  Scenario: a payment can not be completed due to insufficient funds
#    Given a merchant with merchant id "id1" and a account with balance of 50
#    And a customer with a invalid token
#    And an amount of 100
#    When the transactions is initiated
#    Then the transaction is unsuccessful and a error message "Insufficient Funds" is returned