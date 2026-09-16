Feature: Signing in to the storefront
  Only registered, active shoppers should be able to reach the product list,
  and anyone turned away should be told why.

  Background:
    Given the shopper is on the login page

  @smoke
  Scenario: A registered shopper reaches the product list
    When the shopper signs in as the standard user
    Then the product list is shown

  Scenario: A locked account is refused
    When the shopper signs in as the locked out user
    Then the shopper sees the error "locked out"

  Scenario Outline: Bad credentials are refused with a helpful message
    When the shopper signs in with username "<username>" and password "<password>"
    Then the shopper sees the error "<error>"

    Examples:
      | username      | password     | error                  |
      |               |              | Username is required   |
      | standard_user |              | Password is required   |
      | ghost_user    | secret_sauce | do not match any user  |
      | standard_user | wrong_secret | do not match any user  |
