Feature: Transaction Rest Service
  Scenario: sending a transaction request
    Given a transaction request
    When the transaction request is being registered
    Then a "TransactionRequest" event is sent
    And a "TransactionRequestResponse" event is received
    And the transaction response has status successful

