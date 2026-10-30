Feature: User Login, Order Flow, and Step Validations

  @FullFlow @PositiveFlow
  Scenario: Login and add first category product to cart
    Given User is on the login page
    When User select the English Language
    When User enters valid username and password
    And User clicks on login button
    Then User should be redirected to the dashboard
    When User finds the product category and clicks on "Sofas"
    And User adds first product to cart
    And User clicks on Cart icon
    Then User should be redirected to the cart page
    And User verifies the added product details in the cart
    And User verifies the bill amounts including subtotal and total
    When User clicks on Proceed to Checkout button
    Then User should be redirected to the checkout page
    And User verifies delivery address is present on the checkout page
    When User clicks on Proceed to Pay button
    Then User should be redirected to the payment page
    When User selects payment method as "Cash On Delivery"
    And User verifies the submit button text is "Place Order"
    When User clicks on Place Order or Pay Now button
    Then User verifies order is placed or redirected successfully
    When User navigates to My Profile page
    Then User should be redirected to My Profile page
    When User clicks on Logout button
    And User confirms logout by clicking Yes, Logout on popup
    Then User should be logged out successfully

  @LoginValidation
  Scenario: Validate username and password validation on login with multiple invalid credentials in same browser
    Given User is on the login page
    When User select the English Language
    Then User verifies login validation for the following credentials in the same browser:
      | username                         | password         |
      | invalid_test_account@yopmail.com | WrongPass@123    |
      | ashruti1@yopmail.com             | WrongPassword@99 |
      | notanemail                       | Test@1234        |
      | unknown_user@domain.com          | 12345            |

  @StepValidations @CartFlowValidation
  Scenario: Validate each step in the cart and checkout flow after login
    Given User is on the login page
    When User select the English Language
    When User enters valid username and password
    And User clicks on login button
    Then User should be redirected to the dashboard
    When User finds the product category and clicks on "Sofas"
    And User adds first product to cart
    And User clicks on Cart icon
    Then User should be redirected to the cart page
    And User verifies the added product details in the cart
    And User verifies the bill amounts including subtotal and total
    When User clicks on Proceed to Checkout button
    Then User should be redirected to the checkout page
    And User verifies delivery address is present on the checkout page
    When User clicks on Proceed to Pay button
    Then User should be redirected to the payment page
    When User selects payment method as "Cash On Delivery"
    And User verifies the submit button text is "Place Order"
    When User clicks on Place Order or Pay Now button
    Then User verifies order is placed or redirected successfully
    When User navigates to My Profile page
    Then User should be redirected to My Profile page
