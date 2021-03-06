# Authors
# Christian Gernsøe - S163552
# Gustav Utke Kauman - S195396
# Gustav Lintrup Krikholt - s164765
# Niels Bisgaard-Bohr - S202745
# Simon Pontoppidan - S144213
# Theodor Peter Guttesen - S185121
# Thomas Rathsach Strange - S153390
# 
# Main: Gustav Lintrup Kirkholt
#
Feature: Transaction Rest Service
  Scenario: sending a transaction request
    Given a transaction request
    When the transaction request is being registered
    Then a "TransactionRequested" event is sent
    And a "TransactionRequestSuccesfull" event is received
    And the transaction response has status successful

  Scenario: Customer request transaction report
    Given a list of transactions
    When the Customer requests a list of their transactions
    Then a "CustomerReportRequested" event is sent
    And a "ReportResponse" event is received
    And the ReportRequest was successful

  Scenario: Merchant request transaction report
    Given a list of transactions
    When the Merchant requests a list of their transactions
    Then a "MerchantReportRequested" event is sent
    And a "ReportResponse" event is received

  Scenario: Admin request transaction report
    Given a list of transactions
    When the Admin requests a list of all transactions
    Then a "AdminReportRequested" event is sent
    And a "ReportResponse" event is received