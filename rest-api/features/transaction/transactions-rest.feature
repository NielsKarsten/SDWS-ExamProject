Feature: Transaction Rest Service
  Scenario: sending a transaction request
    Given a transaction request
    When the transaction request is being registered
    Then a "TransactionRequest" event is sent
    And a "TransactionRequestResponse" event is received
    And the transaction response has status successful

  Scenario: Customer request transaction report
    Given a list of transactions
    When the Customer requests a list of their transactions
    Then a "CustomerReportRequested" event is sent
    And a "CustomerReportResponse" event is received
    And the ReportRequest was successful

  Scenario: Merchant request transaction report
    Given a list of transactions
    When the Merchant requests a list of their transactions
    Then a "MerchantReportRequested" event is sent
    And a "MerchantReportResponse" event is received

  Scenario: Admin request transaction report
    Given a list of transactions
    When the Admin requests a list of all transactions
    Then a "AdminReportRequested" event is sent
    And a "AdminReportResponse" event is received