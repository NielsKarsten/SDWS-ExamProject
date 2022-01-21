# Authors
# Christian Gernsøe - S163552
# Gustav Utke Kauman - S195396
# Gustav Lintrup Kirkholt - s164765
# Niels Bisgaard-Bohr - S202745
# Simon Pontoppidan - S144213
# Theodor Peter Guttesen - S185121
# Thomas Rathsach Strange - S153390
# 
# Main: Gustav Utke Kauman
#
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
    Then a "ReportResponse" event is sent

  Scenario: No existing Customer transaction
    Given a merchant with an account with a balance of 1000
    And an amount of 100
    When a "TransactionRequested" event is received
    Then a "TransactionRequestInvalid" event is sent with error message "Customer does not exists"
    
  Scenario: No existing merchant transaction
    Given a customer with an account with a balance of 1000
    And an amount of 100
    When a "TransactionRequested" event is received
    Then a "TransactionRequestInvalid" event is sent with error message "Merchant does not exists"
 
   Scenario: Amount not specified
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    When a "TransactionRequested" event is received
    Then a "TransactionRequestInvalid" event is sent with error message "Amount incorrectly specified"


   Scenario: Negative amount specified
    Given a merchant with an account with a balance of 1000
    And a customer with an account with a balance of 1000
    And an amount of -100
    When a "TransactionRequested" event is received
    Then a "TransactionRequestInvalid" event is sent with error message "Amount incorrectly specified"
   