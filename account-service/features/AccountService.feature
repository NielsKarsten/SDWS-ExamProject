Feature: Account service

Scenario: customer registers account
    Given a customer "John" "B" with bank account "1337"
    When the "AccountRegistrationRequested" event is received
    Then the "UserAccountRegistered" event is sent
    And the account is registered

#Scenario: customer closing existing account
    #Given a customer "John" "B" with bank account "13371337" with 1000 kr
    #And the customer registers with DTU Pay
    #When the users account is unregistered
    #Then the account is unregistered