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

#Scenario: customer closing existing account
    #Given a customer "John" "B" with bank account "1337"
    #When the "AccountRegistrationRequested" event is received
    #Then the "UserAccountRegistered" event is sent
    #And the account is registered
    #When the "AccountClosedRequested" event is received
    #Then the "AccountClosed" event is sent
