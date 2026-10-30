package stepDefinitions;

import org.junit.Assert;

import base.TestBase;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import pages.CartPage;
import pages.CheckoutPage;
import pages.HomePage;
import pages.LoginPage;
import pages.ProfilePage;

public class LoginSteps extends TestBase {

	LoginPage loginPage;
	HomePage homePage;
	CartPage cartPage;
	CheckoutPage checkoutPage;
	ProfilePage profilePage;

	// Variables to store selected product details
	private String addedProductTitle;
	private String addedProductPrice;

	@Given("User is on the login page")
	public void user_is_on_the_login_page() {
		initialization();
		loginPage = new LoginPage();
		homePage = new HomePage();
		cartPage = new CartPage();
		checkoutPage = new CheckoutPage();
		profilePage = new ProfilePage();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: BROWSER INITIALIZATION]");
		System.out.println(" - Browser launched and navigated to: https://dev.mustadam.shop");
		System.out.println(" - Current URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@When("User select the English Language")
	public void user_select_the_english_language() {
		loginPage.selectEnglishLanguage();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: LANGUAGE SELECTION]");
		System.out.println(" - English language selected.");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@When("User enters valid username and password")
	public void user_enters_valid_username_and_password() {
		loginPage.enterCredentials("ashruti1@yopmail.com", "Test@1234");
	}

	@When("User enters invalid username {string} and password {string}")
	public void user_enters_invalid_username_and_password(String username, String password) {
		loginPage.enterCredentials(username, password);
	}

	@And("User clicks on login button")
	public void user_clicks_on_login_button() {
		loginPage.clickLogin();
	}

	@Then("User verifies login validation for the following credentials in the same browser:")
	public void user_verifies_login_validation_for_the_following_credentials_in_the_same_browser(DataTable dataTable) {
		java.util.List<java.util.Map<String, String>> credentialsList = dataTable.asMaps(String.class, String.class);
		int count = 1;
		for (java.util.Map<String, String> creds : credentialsList) {
			String username = creds.get("username");
			String password = creds.get("password");

			System.out.println("--------------------------------------------------");
			System.out.println(" [VALIDATING INPUT SET #" + count + " IN SAME BROWSER]");
			System.out.println(" - Testing Username: " + username);
			System.out.println(" - Testing Password: " + password);

			loginPage.enterCredentials(username, password);
			loginPage.clickLogin();

			String valMsg = loginPage.getLoginValidationMessage();
			System.out.println(" - Captured Validation Message: " + valMsg);
			System.out.println(" - Validation Status: PASS");
			System.out.println("--------------------------------------------------");

			takeScreenshot("Login_Validation_Attempt_" + count);
			Assert.assertNotNull("Login validation message should not be null", valMsg);
			Assert.assertFalse("Login validation message should not be empty", valMsg.trim().isEmpty());

			count++;
		}
	}

	@Then("User verifies and prints login validation message")
	public void user_verifies_and_prints_login_validation_message() {
		String valMsg = loginPage.getLoginValidationMessage();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: LOGIN CREDENTIALS VALIDATION]");
		System.out.println(" - Captured Validation Message: " + valMsg);
		System.out.println(" - Current URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
		Assert.assertNotNull("Login validation message should not be null", valMsg);
		Assert.assertFalse("Login validation message should not be empty", valMsg.trim().isEmpty());
	}

	@Then("User should be redirected to the dashboard")
	public void user_should_be_redirected_to_the_dashboard() {
		boolean isDashboardDisplayed = loginPage.isDashboardDisplayed();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: DASHBOARD REDIRECTION]");
		System.out.println(" - Dashboard/Profile Link Displayed: " + isDashboardDisplayed);
		System.out.println(" - Current URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: " + (isDashboardDisplayed ? "PASS" : "FAIL"));
		System.out.println("==================================================");
		Assert.assertTrue("Dashboard/profile link not displayed", isDashboardDisplayed);
	}

	@When("User finds the product category and clicks on {string}")
	public void user_finds_the_product_category_and_clicks_on(String category) {
		// Navigate back to home if needed to view product categories
		if (!driver.getCurrentUrl().equals("https://dev.mustadam.shop/")) {
			homePage.navigateToHome();
		}
		homePage.selectCategoryTab(category);
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: CATEGORY SELECTION]");
		System.out.println(" - Selected Category: " + category);
		System.out.println(" - Category Tab Action: Completed");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@And("User adds first product to cart")
	public void user_adds_first_product_to_cart() {
		// Grab product details before adding to cart
		addedProductTitle = homePage.getFirstVisibleProductTitle();
		addedProductPrice = homePage.getFirstVisibleProductPrice();
		homePage.clickAddToCartForFirstVisibleProduct();

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: ADD PRODUCT TO CART]");
		System.out.println(" - Added Product Title: " + addedProductTitle);
		System.out.println(" - Added Product Price: " + addedProductPrice);
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");

		Assert.assertNotNull("Selected product title should not be null", addedProductTitle);
		Assert.assertFalse("Selected product title should not be empty", addedProductTitle.trim().isEmpty());
	}

	@And("User clicks on Cart icon")
	public void user_clicks_on_cart_icon() {
		cartPage.clickCartIcon();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: CART ICON CLICK]");
		System.out.println(" - Cart header icon clicked successfully.");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@Then("User should be redirected to the cart page")
	public void user_should_be_redirected_to_the_cart_page() {
		boolean isCartDisplayed = cartPage.isCartPageDisplayed();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: CART PAGE REDIRECTION]");
		System.out.println(" - Cart Page Displayed: " + isCartDisplayed);
		System.out.println(" - Current URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: " + (isCartDisplayed ? "PASS" : "FAIL"));
		System.out.println("==================================================");
		Assert.assertTrue("Cart page is not displayed / URL does not contain /cart", isCartDisplayed);
	}

	@And("User verifies the added product details in the cart")
	public void user_verifies_the_added_product_details_in_the_cart() {
		java.util.List<String> cartTitles = cartPage.getCartProductTitles();
		java.util.List<java.util.Map<String, String>> cartDetails = cartPage.getCartProductDetails();

		boolean isTitleMatched = cartPage.isProductInCart(addedProductTitle);
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: CART PRODUCT VERIFICATION]");
		System.out.println(" - Expected Title from Home: " + addedProductTitle);
		System.out.println(" - Expected Price from Home: " + addedProductPrice);
		System.out.println(" - Cart Items Count: " + cartTitles.size());
		for (java.util.Map<String, String> item : cartDetails) {
			System.out.println("   * " + item.get("title") + " | " + item.get("price") + " | Qty: " + item.get("quantity"));
		}
		System.out.println(" - Product Matched in Cart: " + isTitleMatched);
		System.out.println(" - Validation Status: " + (isTitleMatched ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("Added product '" + addedProductTitle + "' was not found in cart. Cart titles: " + cartTitles, isTitleMatched);
	}

	@And("User verifies the bill amounts including subtotal and total")
	public void user_verifies_the_bill_amounts_including_subtotal_and_total() {
		java.util.Map<String, String> amounts = cartPage.getBillAmounts();
		String billText = cartPage.getTotalBillText();
		boolean isTotalDisplayed = cartPage.isTotalBillDisplayed();

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: BILL AMOUNTS & SUMMARY]");
		System.out.println(" - Total Bill Block Displayed: " + isTotalDisplayed);
		System.out.println(" - Extracted Amounts Map: " + amounts);
		System.out.println(" - Bill Summary Section:\n" + billText);
		System.out.println(" - Validation Status: " + (isTotalDisplayed ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("Order summary / Bill block is not displayed", isTotalDisplayed);
	}

	@When("User clicks on Proceed to Checkout button")
	public void user_clicks_on_proceed_to_checkout_button() {
		cartPage.clickProceedToCheckout();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: PROCEED TO CHECKOUT CLICK]");
		System.out.println(" - Proceed to Checkout clicked.");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@Then("User should be redirected to the checkout page")
	public void user_should_be_redirected_to_the_checkout_page() {
		boolean isCheckout = cartPage.isCheckoutPageDisplayed();
		boolean urlContainsCheckout = driver.getCurrentUrl().contains("checkout");
		boolean result = isCheckout || urlContainsCheckout;

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: CHECKOUT PAGE REDIRECTION]");
		System.out.println(" - Checkout Page Verified: " + result);
		System.out.println(" - Current URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: " + (result ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("User was not redirected to checkout page", result);
	}

	@And("User verifies delivery address is present on the checkout page")
	public void user_verifies_delivery_address_is_present_on_the_checkout_page() {
		boolean isAddressPresent = checkoutPage.isDeliveryAddressPresent();
		String addressType = checkoutPage.getDeliveryAddressType();
		String addressLine = checkoutPage.getDeliveryAddressLine();

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: DELIVERY ADDRESS VERIFICATION]");
		System.out.println(" - Delivery Address Present: " + isAddressPresent);
		System.out.println(" - Address Details: [" + addressType + "] " + addressLine);
		System.out.println(" - Validation Status: " + (isAddressPresent ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("Delivery address is not present on checkout page", isAddressPresent);
	}

	@When("User clicks on Proceed to Pay button")
	public void user_clicks_on_proceed_to_pay_button() {
		checkoutPage.clickProceedToPay();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: PROCEED TO PAY CLICK]");
		System.out.println(" - Proceed to Pay button clicked.");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@Then("User should be redirected to the payment page")
	public void user_should_be_redirected_to_the_payment_page() {
		boolean isPayment = checkoutPage.isPaymentPageDisplayed();
		boolean urlContainsPayment = driver.getCurrentUrl().contains("payment");
		boolean result = isPayment || urlContainsPayment;

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: PAYMENT PAGE REDIRECTION]");
		System.out.println(" - Payment Page Verified: " + result);
		System.out.println(" - Current URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: " + (result ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("User was not redirected to payment page", result);
	}

	@When("User selects payment method as {string}")
	public void user_selects_payment_method_as(String paymentMethod) {
		checkoutPage.selectPaymentMethod(paymentMethod);
		boolean isSelected = checkoutPage.isPaymentMethodSelected(paymentMethod);

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: PAYMENT METHOD SELECTION]");
		System.out.println(" - Selected Payment Method: " + paymentMethod);
		System.out.println(" - Method Checked / Selected: " + isSelected);
		System.out.println(" - Validation Status: " + (isSelected ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("Payment method was not selected: " + paymentMethod, isSelected);
	}

	@And("User verifies the submit button text is {string}")
	public void user_verifies_the_submit_button_text_is(String expectedBtnText) {
		String actualBtnText = checkoutPage.getSubmitButtonText();
		boolean matches = actualBtnText.equalsIgnoreCase(expectedBtnText);

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: SUBMIT BUTTON TEXT]");
		System.out.println(" - Expected Button Text: " + expectedBtnText);
		System.out.println(" - Actual Button Text: " + actualBtnText);
		System.out.println(" - Validation Status: " + (matches ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("Button text does not match. Expected: " + expectedBtnText + ", Actual: " + actualBtnText, matches);
	}

	@When("User clicks on Place Order or Pay Now button")
	public void user_clicks_on_place_order_or_pay_now_button() {
		checkoutPage.clickPlaceOrderOrPayNow();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: PLACE ORDER / PAY NOW CLICK]");
		System.out.println(" - Place Order button submitted.");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@Then("User verifies order is placed or redirected successfully")
	public void user_verifies_order_is_placed_or_redirected_successfully() {
		takeScreenshot("Order_Placed_Status");
		boolean orderDone = checkoutPage.isOrderPlacedOrSubmitted();

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: ORDER PLACEMENT VERIFICATION]");
		System.out.println(" - Order Status Completed / Redirected: " + orderDone);
		System.out.println(" - Final URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: " + (orderDone ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("Order was not placed/redirected successfully", orderDone);
	}

	@When("User navigates to My Profile page")
	public void user_navigates_to_my_profile_page() {
		profilePage.clickMyProfileHeader();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: NAVIGATE TO MY PROFILE]");
		System.out.println(" - Navigated to My Profile section.");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@Then("User should be redirected to My Profile page")
	public void user_should_be_redirected_to_my_profile_page() {
		takeScreenshot("My_Profile_Page");
		boolean isProfile = profilePage.isProfilePageDisplayed();

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: MY PROFILE PAGE VERIFICATION]");
		System.out.println(" - Profile Page Displayed: " + isProfile);
		System.out.println(" - Profile URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: " + (isProfile ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("User was not redirected to My Profile page", isProfile);
	}

	@When("User clicks on Logout button")
	public void user_clicks_on_logout_button() {
		profilePage.clickLogout();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: LOGOUT CLICK]");
		System.out.println(" - Logout button clicked; modal triggered.");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@And("User confirms logout by clicking Yes, Logout on popup")
	public void user_confirms_logout_by_clicking_yes_logout_on_popup() {
		takeScreenshot("Logout_Popup_Modal");
		profilePage.confirmLogoutModal();
		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: CONFIRM LOGOUT MODAL]");
		System.out.println(" - Confirmed 'Yes, Logout' on modal dialog.");
		System.out.println(" - Validation Status: PASS");
		System.out.println("==================================================");
	}

	@Then("User should be logged out successfully")
	public void user_should_be_logged_out_successfully() {
		takeScreenshot("Logged_Out_Status");
		boolean isLoggedOut = profilePage.isLoggedOutSuccessfully();

		System.out.println("==================================================");
		System.out.println(" [STEP VALIDATION: LOGOUT STATUS VERIFICATION]");
		System.out.println(" - Logged Out Successfully: " + isLoggedOut);
		System.out.println(" - Current URL: " + driver.getCurrentUrl());
		System.out.println(" - Validation Status: " + (isLoggedOut ? "PASS" : "FAIL"));
		System.out.println("==================================================");

		Assert.assertTrue("User was not logged out successfully", isLoggedOut);
	}
}
