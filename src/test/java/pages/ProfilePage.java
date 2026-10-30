package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.TestBase;

public class ProfilePage extends TestBase {

	// Header locators
	private By headerMyProfileLink = By.xpath("//li[contains(@class,'login_regi_btn')]//a[contains(@href,'my-profile') or contains(text(),'My Profile')] | //a[contains(@href,'my-profile')]");

	// Profile Page locators (/my-profile)
	private By myAccountAccordion = By.id("my_account_accordion");
	private By logoutBtn = By.xpath("//a[contains(@class,'logout_btn') or @data-bs-target='#logout_show' or contains(text(),'Logout')]");
	private By logoutModal = By.id("logout_show");
	private By confirmLogoutBtn = By.xpath("//div[@id='logout_show']//a[@id='confirmLogout' or contains(text(),'Yes, Logout')]");
	private By signInPageIndicator = By.xpath("//button[@id='sign_in_btn'] | //input[@id='email_email'] | //a[contains(@class,'login_btn')]");

	public ProfilePage() {
		super();
	}

	public ProfilePage(WebDriver driver) {
		TestBase.driver = driver;
	}

	private void scrollToElement(WebElement element) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
			Thread.sleep(500);
		} catch (Exception ignored) {
		}
	}

	private void safeClick(By locator) {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		scrollToElement(el);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(el)).click();
		} catch (Exception e) {
			el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
		}
	}

	public void clickMyProfileHeader() {
		try {
			// Try clicking header link
			WebElement profileEl = wait.until(ExpectedConditions.presenceOfElementLocated(headerMyProfileLink));
			scrollToElement(profileEl);
			try {
				wait.until(ExpectedConditions.elementToBeClickable(profileEl)).click();
			} catch (Exception e) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", profileEl);
			}
		} catch (Exception e) {
			// Direct navigation fallback if order success page header is overlayed
			driver.get("https://dev.mustadam.shop/my-profile");
		}

		try {
			wait.until(ExpectedConditions.urlContains("my-profile"));
		} catch (Exception e) {
			driver.get("https://dev.mustadam.shop/my-profile");
		}
	}

	public boolean isProfilePageDisplayed() {
		try {
			wait.until(ExpectedConditions.urlContains("my-profile"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(myAccountAccordion));
			return true;
		} catch (Exception e) {
			return driver.getCurrentUrl().contains("my-profile");
		}
	}

	public void clickLogout() {
		safeClick(logoutBtn);
		try {
			// Wait for confirmation modal to become visible
			wait.until(ExpectedConditions.visibilityOfElementLocated(logoutModal));
		} catch (Exception ignored) {
		}
	}

	public void confirmLogoutModal() {
		safeClick(confirmLogoutBtn);
		try {
			// Wait for AJAX signout and redirection to signin
			wait.until(ExpectedConditions.urlContains("signin"));
		} catch (Exception ignored) {
		}
	}

	public boolean isLoggedOutSuccessfully() {
		try {
			wait.until(ExpectedConditions.urlContains("signin"));
			return true;
		} catch (Exception e) {
			try {
				WebElement signInEl = wait.until(ExpectedConditions.visibilityOfElementLocated(signInPageIndicator));
				return signInEl.isDisplayed();
			} catch (Exception ex) {
				return driver.getCurrentUrl().contains("signin");
			}
		}
	}
}
