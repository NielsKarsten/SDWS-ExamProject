Feature: Transaction Rest Service
  Scenario: sending a transaction request
    When a "TransactionRestRequest" event is received
    Then a "TransactionRestRequestResponse" event is sent
    And the transaction response has status successful

