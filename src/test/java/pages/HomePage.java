package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import base.TestBase;

public class HomePage extends TestBase {

	// Tab locators
	private By allTab = By.cssSelector("button[data-bs-target='#all_product']");
	private By sofasTab = By.cssSelector("button[data-bs-target='#sofas_product']");
	private By tablesTab = By.cssSelector("button[data-bs-target='#tables_product']");
	private By decorTab = By.cssSelector("button[data-bs-target='#decor_product']");
	private By chairsTab = By.cssSelector("button[data-bs-target='#chairs_product']");
	private By outdoorsTab = By.cssSelector("button[data-bs-target='#outdoors_product']");
	private By storageTab = By.cssSelector("button[data-bs-target='#storage_product']");

	// Active tab product items & controls
	private By activeProducts = By.cssSelector(".tab-pane.active li.product_item.slick-active");
	private By slickNextBtn = By.cssSelector(".tab-pane.active button.slick-next");
	private By slickPrevBtn = By.cssSelector(".tab-pane.active button.slick-prev");

	public HomePage() {
		super();
	}

	public HomePage(WebDriver driver) {
		TestBase.driver = driver;
	}

	private void safeClick(By locator) {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		try {
			wait.until(ExpectedConditions.elementToBeClickable(el)).click();
		} catch (Exception e) {
			// Re-find element to handle stale references after page reload
			el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
		}
	}

	public void selectCategoryTab(String category) {
		By tabLocator;
		switch (category.toLowerCase().trim()) {
		case "sofas":
			tabLocator = sofasTab;
			break;
		case "tables":
			tabLocator = tablesTab;
			break;
		case "decor":
			tabLocator = decorTab;
			break;
		case "chairs":
			tabLocator = chairsTab;
			break;
		case "outdoors":
			tabLocator = outdoorsTab;
			break;
		case "storage":
			tabLocator = storageTab;
			break;
		case "all":
		default:
			tabLocator = allTab;
			break;
		}

		// Scroll to the category tabs section so it is visible on screen
		scrollToElement(By.id("myTab"));

		safeClick(tabLocator);

		// Wait for active class on the tab button
		try {
			wait.until(ExpectedConditions.attributeContains(tabLocator, "class", "active"));
		} catch (Exception ignored) {
		}
	}

	// Scroll to the product category section so actions are visible
	private void scrollToElement(By locator) {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", el);
		try { Thread.sleep(500); } catch (InterruptedException ignored) {}
	}

	public boolean isProductSectionDisplayed() {
		try {
			WebElement tabSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("myTab")));
			return tabSection.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public int getVisibleProductCount() {
		try {
			return driver.findElements(activeProducts).size();
		} catch (Exception e) {
			return 0;
		}
	}

	public void clickNextProductSlide() {
		safeClick(slickNextBtn);
	}

	public void navigateToHome() {
		driver.get("https://dev.mustadam.shop");
		wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete' ? true : null;"));
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("site_loader")));
		} catch (Exception ignored) {
		}
	}

	public void clickFirstProductInActiveTab() {
		By firstProductLink = By.xpath("(//div[contains(@class,'tab-pane') and contains(@class,'active')]//li[contains(@class,'product_item') and contains(@class,'slick-active')]//a)[1]");
		safeClick(firstProductLink);
	}

	public void clickAddToCartForFirstVisibleProduct() {
		By firstAddToCartBtn = By.xpath("(//div[contains(@class,'tab-pane') and contains(@class,'active')]//li[contains(@class,'product_item') and contains(@class,'slick-active')]//button[contains(@class,'add-to-cart-btn')])[1]");
		// Scroll to the product so the Add to Cart button is visible
		scrollToElement(firstAddToCartBtn);
		safeClick(firstAddToCartBtn);
		try {
			Thread.sleep(1500);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("site_loader")));
		} catch (Exception ignored) {
		}
	}

	public void clickBuyNowForFirstVisibleProduct() {
		By firstBuyNowBtn = By.xpath("(//div[contains(@class,'tab-pane') and contains(@class,'active')]//li[contains(@class,'product_item') and contains(@class,'slick-active')]//button[contains(@class,'buy-now-btn')])[1]");
		safeClick(firstBuyNowBtn);
	}

	public String getFirstVisibleProductTitle() {
		By titleLocator = By.xpath("(//div[contains(@class,'tab-pane') and contains(@class,'active')]//li[contains(@class,'product_item') and contains(@class,'slick-active')]//h5)[1]");
		WebElement titleEl = wait.until(ExpectedConditions.presenceOfElementLocated(titleLocator));
		String titleAttr = titleEl.getAttribute("title");
		if (titleAttr != null && !titleAttr.trim().isEmpty()) {
			return titleAttr.trim();
		}
		String text = titleEl.getText().trim();
		if (text.contains("\n")) {
			text = text.split("\n")[0].trim();
		}
		return text;
	}

	public String getFirstVisibleProductPrice() {
		try {
			By btnLocator = By.xpath("(//div[contains(@class,'tab-pane') and contains(@class,'active')]//li[contains(@class,'product_item') and contains(@class,'slick-active')]//button[contains(@class,'add-to-cart-btn')])[1]");
			WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(btnLocator));
			String priceAttr = btn.getAttribute("data-product-price");
			if (priceAttr != null && !priceAttr.trim().isEmpty()) {
				return priceAttr.trim();
			}
		} catch (Exception ignored) {
		}
		return "";
	}
}
