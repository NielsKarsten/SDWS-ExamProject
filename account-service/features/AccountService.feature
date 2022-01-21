# Authors
# Christian Gernsøe - S163552
# Gustav Utke Kauman - S195396
# Gustav Lintrup Krikholt - s164765
# Niels Bisgaard-Bohr - S202745
# Simon Pontoppidan - S144213
# Theodor Peter Guttesen - S185121
# Thomas Rathsach Strange - S153390
# 
# Main: Simon Pontoppidan
#
Feature: Account service

Scenario: customer registers account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountRegistrationRequested" event is received
    Then the "UserAccountRegistered" event is sent
    And the account is registered

Scenario: user accountInfo is requested
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountRegistrationRequested" event is received 
    Then the "UserAccountRegistered" event is sent   
    When the "UserAccountInfoRequested" event is received
    Then the "UserAccountInfoResponse" event is sent

Scenario: Non-existing user accountInfo is requested
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "UserAccountInfoRequested" event is received with no user
    Then the "UserAccountInvalid" event is sent

Scenario: customer closing existing account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountRegistrationRequested" event is received 
    Then the "UserAccountRegistered" event is sent  
    When the "AccountClosedRequested" event is received
    Then the "RetireUserAccountTokensRequest" event is sent
    And the "ClosedUserAccountTokensRetired" event is received
    Then the "AccountClosedResponse" event is sent

Scenario: customer closing non-existing account
    When the "AccountClosedRequested" event is received with no user
    Then the "UserAccountInvalid" event is sent

Scenario: user wants to verify whether account exists
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountRegistrationRequested" event is received
    Then the "UserAccountRegistered" event is sent
    When the "VerifyUserAccountExistsRequest" event is received
    Then the "VerifyUserAccountExistsResponse" event is sent

Scenario: user wants to verify whether non-existing account exists
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "VerifyUserAccountExistsRequest" event is received
    Then the "UserAccountInvalid" event is sent

Scenario: close a users tokens when retiring account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountRegistrationRequested" event is received
    Then the "UserAccountRegistered" event is sent
    When the "AccountClosedRequested" event is received
    Then the "RetireUserAccountTokensRequest" event is sent
    And the "ClosedUserAccountTokensRetired" event is received

Scenario: error when closing a non existing user
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountRegistrationRequested" event is received
    Then the "UserAccountRegistered" event is sent
    When the "AccountClosedRequested" event is received
    Then the "RetireUserAccountTokensRequest" event is sent
    And the "AccountClosedRetireTokenRequestInvalid" event is received
