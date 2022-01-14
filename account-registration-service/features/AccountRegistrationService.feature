Feature: Account service

Scenario: user registers account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the user is being registered
    Then the "AccountRegistrationRequested" event is sent
    When the "UserAccountRegistered" event is received
    Then the account is registered
    
Scenario: user asd account
    Given a user "Johnny" "Bravo" with bank account "1337"
    When the user is being registered
    Then the "AccountRegistrationRequested" event is sent
    When the "UserAccountRegistered" event is received
    Then the account is registered
    
 #Scenario: user accountInfo is requested
#	  Given a user "Johnny" "Bravo" with bank account "1337"
#	  When the user is being registered
#	  Then the "AccountRegistrationRequested" event is sent
#	  When the "UserAccountRegistered" event is received   
#	  Then the account is registered
  #Then the "UserAccountInfoRequested" event is sent
  #When the "UserAccountInfoResponse" event is received
  #Then the account information is returned

# Scenario: Non-existing user accountInfo is requested
#     Given a user "Johnny" "Bravo" with bank account "1337"
#     When the "UserAccountInfoRequested" event is received
#     Then the "UserAccountInfoResponse" event is sent

# Scenario: customer closing existing account
#     Given a user "Johnny" "Bravo" with bank account "1337"
#     When the "AccountRegistrationRequested" event is received 
#     Then the "UserAccountRegistered" event is sent  
#     When the "AccountClosedRequested" event is received
#     Then the "AccountClosedResponse" event is sent

# Scenario: customer closing non-existing account
#     Given a user "Johnny" "Bravo" with bank account "1337"
#     When the "AccountClosedRequested" event is received
#     Then the "AccountClosedResponse" event is sent