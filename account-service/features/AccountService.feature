Feature: Account service

Scenario: customer registers account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountRegistrationRequested" event is received
    Then the "UserAccountRegistered" event is sent
    And the account is registered

Scenario: customer registers account alt
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
    When the "UserAccountInfoRequested" event is received
    Then the "UserAccountInfoResponse" event is sent

Scenario: customer closing existing account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountRegistrationRequested" event is received 
    Then the "UserAccountRegistered" event is sent  
    When the "AccountClosedRequested" event is received
    Then the "AccountClosedResponse" event is sent

Scenario: customer closing non-existing account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the "AccountClosedRequested" event is received
    Then the "AccountClosedResponse" event is sent
