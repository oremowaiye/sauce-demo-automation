Feature: Buying a product
  A shopper should be able to put a product in the basket, pay for it, and be
  charged the price on the label plus tax - no more, no less.

  Background:
    Given the shopper is on the login page
    When the shopper signs in as the standard user

  @smoke
  Scenario: A shopper buys a single product
    When the shopper adds "Sauce Labs Backpack" to the cart
    Then the cart badge shows 1
    When the shopper opens the cart
    Then the cart contains "Sauce Labs Backpack"
    When the shopper starts checkout
    And the shopper provides valid delivery details
    Then the order total is the item total plus tax
    When the shopper confirms the order
    Then the order is confirmed
    And the cart is emptied

  Scenario: A shopper changes their mind before checking out
    When the shopper adds "Sauce Labs Bike Light" to the cart
    And the shopper opens the cart
    And the shopper removes "Sauce Labs Bike Light" from the cart
    Then the cart is empty

  Scenario Outline: Delivery details are mandatory
    When the shopper adds "Sauce Labs Backpack" to the cart
    And the shopper opens the cart
    And the shopper starts checkout
    And the shopper submits delivery details "<first>", "<last>" and "<postcode>"
    Then the checkout form shows the error "<error>"

    Examples:
      | first | last    | postcode | error                   |
      |       | Mowaiye | SE1 9SG  | First Name is required  |
      | Ore   |         | SE1 9SG  | Last Name is required   |
      | Ore   | Mowaiye |          | Postal Code is required |
