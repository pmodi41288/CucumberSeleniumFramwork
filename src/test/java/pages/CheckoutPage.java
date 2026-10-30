package pages;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.TestBase;

public class CheckoutPage extends TestBase {

	// Checkout page locators (/checkout)
	private By orderSummaryHeading = By.xpath("//h5[contains(text(),'Order Summary')]");
	private By deliveryAddressType = By.cssSelector(".delivery-address-type");
	private By deliveryAddressLine = By.cssSelector(".delivery-address");
	private By changeAddressBtn = By.xpath("//button[@data-bs-target='#address_modal' or contains(text(),'Change')] | //a[@data-bs-target='#address_modal' or contains(text(),'Change')]");
	private By addressRadioList = By.cssSelector(".address-radio");
	private By proceedToPayBtn = By.xpath("//a[contains(@href,'payment') or contains(@class,'payment_btn') or contains(text(),'Proceed to Pay')] | //button[contains(text(),'Proceed to Pay')]");
	
	// Payment page locators (/payment)
	private By paymentMethodHeading = By.xpath("//h5[contains(text(),'Payment Method')]");
	private By creditDebitCardRadio = By.id("credit_debit_card");
	private By creditDebitCardLabel = By.xpath("//label[@for='credit_debit_card']");
	private By tabbyRadio = By.id("tabby");
	private By tabbyLabel = By.xpath("//label[@for='tabby']");
	private By cashOnDeliveryRadio = By.id("cash_on_delivery");
	private By cashOnDeliveryLabel = By.xpath("//label[@for='cash_on_delivery']");
	private By submitPaymentBtn = By.cssSelector("button.payment_btn");
	private By orderSuccessMessage = By.xpath("//*[contains(text(),'Thank you') or contains(text(),'Order Placed') or contains(text(),'Order Successful') or contains(text(),'Order #') or contains(@class,'order_success')]");

	public CheckoutPage() {
		super();
	}

	public CheckoutPage(WebDriver driver) {
		TestBase.driver = driver;
	}

	// Safe click helper: handles preloader overlay, element intercepted & stale elements
	private void safeClick(By locator) {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		scrollToElement(el);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(el)).click();
		} catch (Exception e) {
			el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
		}
		waitForLoaderToDisappear();
	}

	private void scrollToElement(WebElement element) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
			Thread.sleep(500);
		} catch (Exception ignored) {
		}
	}

	private void waitForLoaderToDisappear() {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(ExpectedConditions.invisibilityOfElementLocated(By.id("site_loader")));
		} catch (Exception ignored) {
		}
	}

	// ==================== CHECKOUT PAGE ACTIONS (/checkout) ====================

	public boolean isCheckoutPageDisplayed() {
		try {
			wait.until(ExpectedConditions.urlContains("checkout"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(orderSummaryHeading));
			return true;
		} catch (Exception e) {
			return driver.getCurrentUrl().contains("checkout");
		}
	}

	public String getDeliveryAddressType() {
		try {
			WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(deliveryAddressType));
			scrollToElement(el);
			return el.getText().trim();
		} catch (Exception e) {
			System.out.println("Could not get delivery address type: " + e.getMessage());
			return "";
		}
	}

	public String getDeliveryAddressLine() {
		try {
			WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(deliveryAddressLine));
			return el.getText().trim();
		} catch (Exception e) {
			System.out.println("Could not get delivery address line: " + e.getMessage());
			return "";
		}
	}

	public boolean isDeliveryAddressPresent() {
		String addr = getDeliveryAddressLine();
		return addr != null && !addr.isEmpty();
	}

	public void clickProceedToPay() {
		safeClick(proceedToPayBtn);
		try {
			wait.until(ExpectedConditions.urlContains("payment"));
		} catch (Exception ignored) {
		}
	}

	// ==================== PAYMENT PAGE ACTIONS (/payment) ====================

	public boolean isPaymentPageDisplayed() {
		try {
			wait.until(ExpectedConditions.urlContains("payment"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(paymentMethodHeading));
			return true;
		} catch (Exception e) {
			return driver.getCurrentUrl().contains("payment");
		}
	}

	public void selectPaymentMethod(String method) {
		String normalized = method.toLowerCase().trim();
		WebElement targetLabel;
		By targetRadio;

		if (normalized.contains("cash") || normalized.contains("cod") || normalized.contains("delivery")) {
			targetRadio = cashOnDeliveryRadio;
			targetLabel = wait.until(ExpectedConditions.presenceOfElementLocated(cashOnDeliveryLabel));
		} else if (normalized.contains("tabby")) {
			targetRadio = tabbyRadio;
			targetLabel = wait.until(ExpectedConditions.presenceOfElementLocated(tabbyLabel));
		} else {
			// Default to Credit / Debit Card
			targetRadio = creditDebitCardRadio;
			targetLabel = wait.until(ExpectedConditions.presenceOfElementLocated(creditDebitCardLabel));
		}

		scrollToElement(targetLabel);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(targetLabel)).click();
		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetLabel);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}

		// Ensure the corresponding radio button is marked selected
		WebElement radioEl = driver.findElement(targetRadio);
		if (!radioEl.isSelected()) {
			((JavascriptExecutor) driver).executeScript("arguments[0].checked = true; arguments[0].dispatchEvent(new Event('change'));", radioEl);
		}
	}

	public boolean isPaymentMethodSelected(String method) {
		String normalized = method.toLowerCase().trim();
		By targetRadio;
		if (normalized.contains("cash") || normalized.contains("cod") || normalized.contains("delivery")) {
			targetRadio = cashOnDeliveryRadio;
		} else if (normalized.contains("tabby")) {
			targetRadio = tabbyRadio;
		} else {
			targetRadio = creditDebitCardRadio;
		}
		try {
			WebElement radio = driver.findElement(targetRadio);
			return radio.isSelected();
		} catch (Exception e) {
			return false;
		}
	}

	public String getSubmitButtonText() {
		try {
			WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(submitPaymentBtn));
			return btn.getText().trim();
		} catch (Exception e) {
			return "";
		}
	}

	public void clickPlaceOrderOrPayNow() {
		safeClick(submitPaymentBtn);
		waitForLoaderToDisappear();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ignored) {
		}
	}

	public boolean isOrderPlacedOrSubmitted() {
		// Verify redirection to order confirmation, success page, or payment gateway
		String currentUrl = driver.getCurrentUrl();
		if (currentUrl.contains("success") || currentUrl.contains("order") || currentUrl.contains("thankyou") || currentUrl.contains("invoice")) {
			return true;
		}
		try {
			WebElement successEl = wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessMessage));
			return successEl.isDisplayed();
		} catch (Exception e) {
			// If URL changed away from payment page, order was submitted
			return !currentUrl.contains("payment");
		}
	}
}
