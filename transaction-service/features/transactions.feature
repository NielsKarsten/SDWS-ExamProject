Feature: Transaction Service
  Scenario: a payment is successful
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And an amount of 100
    When the transaction is initiated
    Then the transaction is successful
    And the transaction is saved

  Scenario: receiving a transaction request
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And an amount of 100
    When a "TransactionRequested" event is received
    Then a "TransactionRequestSuccesfull" event is sent

  Scenario: Customer report is requested
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And a list of transactions
    When a "CustomerReportRequested" event is received
    Then a "ReportResponse" event is sent

  Scenario: Merchant report is requested
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And a list of transactions
    When a "MerchantReportRequested" event is received
    Then a "ReportResponse" event is sent
   
  Scenario: Admin report is requested
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And a list of transactions
    When a "AdminReportRequested" event is received
    Then a "Response" event is sent