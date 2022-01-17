Feature: Account service

Scenario: user registers account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the user is being registered
    Then the "AccountRegistrationRequested" event is sent
    When the "UserAccountRegistered" event is received
    Then the account is registered
    
Scenario: user accountInfo is requested
	  Given a user "Johnny" "Bravo" with bank account "1337"
    When the user account id is requested
    Then the "UserAccountInfoRequested" event is sent
    When the "UserAccountInfoResponse" event is received
    Then the account information is returned

 Scenario: customer closing existing account
     Given a user "Johnny" "Bravo" with bank account "1337"
     When the user account is closed
     When the "AccountClosedRequested" event is sent
     Then the "AccountClosedResponse" event is received
     Then the account is closed