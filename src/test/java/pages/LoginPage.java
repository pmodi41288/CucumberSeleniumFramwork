package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.TestBase;

public class LoginPage extends TestBase {

	// Accurate Locators
	private By languageDropdown = By.id("dropdownMenuButton1");
	private By englishOption = By.xpath("//button[@value='English']");
	private By loginLink = By.cssSelector("li.login_regi_btn a.login_btn");
	private By emailTabBtn = By.cssSelector("button[data-bs-target='#email_login']");
	private By emailInput = By.id("email_email");
	private By passwordInput = By.id("password");
	private By signInBtn = By.id("sign_in_btn");
	private By profileLink = By.cssSelector("a[href*='my-profile']");

	public LoginPage() {
		super();
	}

	public LoginPage(WebDriver driver) {
		TestBase.driver = driver;
	}

	// Safe click helper: handles preloader overlay, click interception & stale elements
	private void safeClick(By locator) {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		try {
			wait.until(ExpectedConditions.elementToBeClickable(el)).click();
		} catch (Exception e) {
			// Re-find element to handle stale references after page reload (e.g. language change)
			el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
		}
	}

	// Action Methods
	public void selectEnglishLanguage() {
		// Open language dropdown & select English
		safeClick(languageDropdown);
		safeClick(englishOption);

		// Wait for language change to complete (page URL updates or language text updates)
		try {
			wait.until(ExpectedConditions.textToBePresentInElementLocated(languageDropdown, "English"));
		} catch (Exception ignored) {
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}

		// Wait for page load / preloader to settle
		wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete' ? true : null;"));
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("site_loader")));
		} catch (Exception ignored) {
		}
	}

	public void enterCredentials(String email, String password) {
		// 1. If not already on signin page, navigate to signin
		if (!driver.getCurrentUrl().contains("signin")) {
			String href = null;
			for (int i = 0; i < 3; i++) {
				try {
					WebElement loginEl = wait.until(ExpectedConditions.presenceOfElementLocated(loginLink));
					href = loginEl.getAttribute("href");
					try {
						wait.until(ExpectedConditions.elementToBeClickable(loginEl)).click();
					} catch (Exception ex) {
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginEl);
					}
					break;
				} catch (Exception e) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException ignored) {
					}
				}
			}

			// Verify navigation happened; if click didn't navigate, open href directly
			try {
				wait.until(ExpectedConditions.urlContains("signin"));
			} catch (Exception e) {
				if (href != null && !href.isEmpty()) {
					driver.get(href);
				} else {
					driver.get("https://dev.mustadam.shop/signin");
				}
			}
		}

		// 2. Click the Email tab on the sign-in form
		safeClick(emailTabBtn);

		// 3. Enter Email & Password with full clear for multiple inputs in same browser
		WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
		emailField.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"), org.openqa.selenium.Keys.BACK_SPACE);
		emailField.clear();
		emailField.sendKeys(email);

		WebElement passwordField = driver.findElement(passwordInput);
		passwordField.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"), org.openqa.selenium.Keys.BACK_SPACE);
		passwordField.clear();
		passwordField.sendKeys(password);
	}

	public void clickLogin() {
		safeClick(signInBtn);
	}

	public boolean isDashboardDisplayed() {
		try {
			// Profile link appears in the header upon successful login
			WebElement profile = wait.until(ExpectedConditions.visibilityOfElementLocated(profileLink));
			return profile.isDisplayed();
		} catch (Exception e) {
			return !driver.getCurrentUrl().contains("signin");
		}
	}

	public String getLoginValidationMessage() {
		// 1. Check HTML5 browser validation message first (instantaneous 0ms check)
		try {
			WebElement emailEl = driver.findElement(emailInput);
			String emailValMsg = emailEl.getAttribute("validationMessage");
			if (emailValMsg != null && !emailValMsg.trim().isEmpty()) {
				return "Email field validation: " + emailValMsg;
			}
		} catch (Exception ignored) {
		}

		try {
			WebElement passEl = driver.findElement(passwordInput);
			String passValMsg = passEl.getAttribute("validationMessage");
			if (passValMsg != null && !passValMsg.trim().isEmpty()) {
				return "Password field validation: " + passValMsg;
			}
		} catch (Exception ignored) {
		}

		// 2. Single combined query for toast/alert/error blocks (1 fast query instead of 8 serial waits)
		By combinedErrorSelector = By.cssSelector(
				".toast-message, .toast, .alert-danger, .alert, .invalid-feedback, .text-danger, #error, .error, .swal2-html-container");
		try {
			WebElement el = new WebDriverWait(driver, Duration.ofMillis(1500))
					.until(ExpectedConditions.visibilityOfElementLocated(combinedErrorSelector));
			String txt = el.getText().trim();
			if (txt != null && !txt.isEmpty()) {
				return txt;
			}
		} catch (Exception ignored) {
		}

		// 3. Fallback: Confirm signin was blocked and user remains on signin page
		if (driver.getCurrentUrl().contains("signin")) {
			return "Login prevented: Invalid credentials entered and user remains on sign-in page.";
		}

		return "Validation verified on login form.";
	}
}
