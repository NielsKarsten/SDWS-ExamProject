Feature: Transaction Rest Service
  Scenario: sending a transaction request
    When a "TransactionRestRequest" event is sent
    Then a "TransactionRestRequestResponse" event is received
    And the transaction response has status successful

