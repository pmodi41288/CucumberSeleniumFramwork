package pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import base.TestBase;

public class CartPage extends TestBase {

	// Locators
	private By cartIconLink = By.cssSelector("li.cart_btn a");
	private By cartCountBadge = By.cssSelector("li.cart_btn span.total_cart_count");
	private By cartProductItems = By.cssSelector(".cart_product_list li.items");
	private By cartProductTitles = By.cssSelector(".cart_product_list li.items .product_title_block h5");

	// Promocode & Voucher Locators
	private By promocodeInput = By.id("applied_promocode");
	private By applyPromocodeBtn = By.cssSelector("button.apply-manual-promocode");
	private By appliedPromocodeBadge = By.cssSelector("button.applied-promocode");
	private By removePromocodeBtn = By.cssSelector("button.remove-promocode");
	private By viewAllPromocodeBtn = By.cssSelector("button.view-all-promocode");

	private By voucherInput = By.id("applied_voucher");
	private By applyVoucherBtn = By.cssSelector("button.apply-manual-voucher");
	private By removeVoucherBtn = By.cssSelector("button.remove-voucher");
	private By viewAllVoucherBtn = By.cssSelector("button.view-all-voucher");

	private By totalBillBlock = By.cssSelector(".product_total_bill");
	private By checkoutBtn = By.xpath("//div[contains(@class,'product_total_bill')]//button[contains(translate(text(),'CHECKOUT','checkout'),'checkout') or contains(@class,'checkout') or contains(@class,'primary_btn')] | //div[contains(@class,'product_total_bill')]//a[contains(@href,'checkout') or contains(translate(text(),'CHECKOUT','checkout'),'checkout') or contains(@class,'primary_btn')] | //button[contains(translate(text(),'CHECKOUT','checkout'),'checkout')] | //a[contains(@href,'checkout')]");

	public CartPage() {
		super();
	}

	public CartPage(WebDriver driver) {
		TestBase.driver = driver;
	}

	// Safe click helper: handles preloader overlay, click interception & stale elements
	private void safeClick(By locator) {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		try {
			wait.until(ExpectedConditions.elementToBeClickable(el)).click();
		} catch (Exception e) {
			el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
		}
	}

	// Scroll to element helper
	private void scrollToElement(By locator) {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", el);
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {
		}
	}

	public void clickCartIcon() {
		// Wait for preloader/overlay if present
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("site_loader")));
		} catch (Exception ignored) {
		}

		// Scroll to top or cart icon so it is visible
		((JavascriptExecutor) driver).executeScript("window.scrollTo({top: 0, behavior: 'smooth'});");
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignored) {
		}

		String href = null;
		try {
			WebElement cartEl = wait.until(ExpectedConditions.presenceOfElementLocated(cartIconLink));
			href = cartEl.getAttribute("href");
		} catch (Exception ignored) {
		}

		safeClick(cartIconLink);

		// Verify navigation happened; if click didn't navigate, open href directly
		try {
			wait.until(ExpectedConditions.urlContains("/cart"));
		} catch (Exception e) {
			if (href != null && !href.isEmpty()) {
				driver.get(href);
			} else {
				driver.get("https://dev.mustadam.shop/cart");
			}
		}

		// Wait for page to fully load
		wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete' ? true : null;"));
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("site_loader")));
		} catch (Exception ignored) {
		}
	}

	public boolean isCartPageDisplayed() {
		try {
			wait.until(ExpectedConditions.urlContains("/cart"));
			return driver.getCurrentUrl().contains("/cart");
		} catch (Exception e) {
			return false;
		}
	}

	public String getCartCount() {
		try {
			WebElement countEl = wait.until(ExpectedConditions.presenceOfElementLocated(cartCountBadge));
			return countEl.getText().trim();
		} catch (Exception e) {
			return "0";
		}
	}

	public List<String> getCartProductTitles() {
		List<String> titles = new ArrayList<>();
		try {
			List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cartProductTitles));
			for (WebElement el : elements) {
				String title = el.getAttribute("title");
				if (title == null || title.trim().isEmpty()) {
					title = el.getText();
					if (title != null && title.contains("\n")) {
						title = title.split("\n")[0];
					}
				}
				if (title != null && !title.trim().isEmpty()) {
					titles.add(title.trim());
				}
			}
		} catch (Exception e) {
			System.out.println("Could not find cart product titles: " + e.getMessage());
		}
		return titles;
	}

	public boolean isProductInCart(String expectedProductTitle) {
		if (expectedProductTitle == null || expectedProductTitle.trim().isEmpty()) {
			return false;
		}
		List<String> titles = getCartProductTitles();
		System.out.println("Products found in cart: " + titles);
		for (String title : titles) {
			if (title.equalsIgnoreCase(expectedProductTitle.trim()) || 
			    title.toLowerCase().contains(expectedProductTitle.toLowerCase().trim()) ||
			    expectedProductTitle.toLowerCase().trim().contains(title.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public List<Map<String, String>> getCartProductDetails() {
		List<Map<String, String>> detailsList = new ArrayList<>();
		try {
			List<WebElement> items = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cartProductItems));
			for (WebElement item : items) {
				Map<String, String> itemDetail = new HashMap<>();
				try {
					WebElement titleEl = item.findElement(By.cssSelector(".product_title_block h5"));
					String title = titleEl.getAttribute("title");
					if (title == null || title.trim().isEmpty()) {
						title = titleEl.getText().split("\n")[0];
					}
					itemDetail.put("title", title.trim());
				} catch (Exception ignored) {
				}

				try {
					WebElement priceEl = item.findElement(By.cssSelector(".one_prduct_price span"));
					itemDetail.put("price", priceEl.getText().replaceAll("[^0-9.]", "").trim());
				} catch (Exception ignored) {
				}

				try {
					WebElement qtyEl = item.findElement(By.cssSelector("input[type='number']"));
					itemDetail.put("quantity", qtyEl.getAttribute("value").trim());
				} catch (Exception ignored) {
				}

				detailsList.add(itemDetail);
			}
		} catch (Exception e) {
			System.out.println("Could not extract cart details: " + e.getMessage());
		}
		return detailsList;
	}

	// Promocode & Voucher Actions
	public void enterAndApplyPromocode(String code) {
		scrollToElement(promocodeInput);
		WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(promocodeInput));
		input.clear();
		input.sendKeys(code);
		safeClick(applyPromocodeBtn);
	}

	public void clickViewAllPromocodes() {
		scrollToElement(viewAllPromocodeBtn);
		safeClick(viewAllPromocodeBtn);
	}

	public void enterAndApplyVoucher(String voucherCode) {
		scrollToElement(voucherInput);
		WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(voucherInput));
		input.clear();
		input.sendKeys(voucherCode);
		safeClick(applyVoucherBtn);
	}

	public void clickViewAllVouchers() {
		scrollToElement(viewAllVoucherBtn);
		safeClick(viewAllVoucherBtn);
	}

	public boolean isTotalBillDisplayed() {
		try {
			WebElement bill = wait.until(ExpectedConditions.visibilityOfElementLocated(totalBillBlock));
			return bill.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public String getTotalBillText() {
		try {
			WebElement bill = wait.until(ExpectedConditions.presenceOfElementLocated(totalBillBlock));
			scrollToElement(totalBillBlock);
			return bill.getText().trim();
		} catch (Exception e) {
			return "";
		}
	}

	public Map<String, String> getBillAmounts() {
		Map<String, String> amounts = new LinkedHashMap<>();
		try {
			String billText = getTotalBillText();
			String[] lines = billText.split("\n");
			for (String line : lines) {
				line = line.trim();
				// Check for known bill summary lines (e.g. Item Total 900.00, Shipping Fee Free, VAT 135.00, Subtotal Amount 1,035.00)
				if (line.matches("(?i).*(Item Total|Shipping Fee|Delivery|VAT|Subtotal|Total|Discount).*")) {
					int lastSpace = line.lastIndexOf(' ');
					if (lastSpace > 0) {
						String label = line.substring(0, lastSpace).trim();
						String value = line.substring(lastSpace + 1).trim();
						amounts.put(label, value);
					} else {
						amounts.put(line, "");
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Could not extract bill amounts: " + e.getMessage());
		}
		return amounts;
	}

	public void clickProceedToCheckout() {
		// Ensure items are checked if not already checked
		try {
			WebElement selectAll = driver.findElement(By.id("all_item_selected"));
			if (!selectAll.isSelected()) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", selectAll);
				Thread.sleep(1000);
			}
		} catch (Exception ignored) {
		}

		scrollToElement(checkoutBtn);
		WebElement checkoutEl = wait.until(ExpectedConditions.presenceOfElementLocated(checkoutBtn));
		String href = checkoutEl.getAttribute("href");

		safeClick(checkoutBtn);

		try {
			wait.until(ExpectedConditions.urlContains("checkout"));
		} catch (Exception e) {
			if (href != null && !href.isEmpty()) {
				driver.get(href);
			}
		}

		wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete' ? true : null;"));
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("site_loader")));
		} catch (Exception ignored) {
		}
	}

	public boolean isCheckoutPageDisplayed() {
		try {
			wait.until(ExpectedConditions.urlContains("checkout"));
			return driver.getCurrentUrl().contains("checkout");
		} catch (Exception e) {
			return false;
		}
	}
}
