Feature: Transaction Rest Service
  Scenario: sending a transaction request
    When a "TransactionRequest" event is sent
    Then a "TransactionRequestResponse" event is received
    And the transaction response has status successful

