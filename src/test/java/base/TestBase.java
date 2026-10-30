package base;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.Scenario;

public class TestBase {

	public static WebDriver driver;
	public static WebDriverWait wait;

	public static void initialization() {
		ChromeOptions options = new ChromeOptions();

		driver = new ChromeDriver(options);
		driver.manage().window().maximize();

		// Global Waits
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

		// Global Explicit Wait instance
		wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		// Navigate to URL
		driver.get("https://dev.mustadam.shop");
	}

	/**
	 * Captures a screenshot as a byte array and also writes a copy to disk under 'screenshots/'.
	 * Can be used anywhere across page classes or step definitions.
	 *
	 * @param screenshotName Descriptive name for the screenshot
	 * @return byte array of the screenshot (ready for attaching to reports)
	 */
	public static byte[] takeScreenshot(String screenshotName) {
		if (driver == null) {
			System.out.println("Driver is not initialized. Cannot take screenshot: " + screenshotName);
			return new byte[0];
		}
		try {
			byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

			// Save screenshot file to screenshots/ directory directly from bytes (avoids duplicate browser freeze)
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String safeName = screenshotName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
			File destDir = new File("screenshots");
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			File destFile = new File(destDir, safeName + "_" + timestamp + ".png");
			Files.write(destFile.toPath(), screenshotBytes);
			System.out.println("Screenshot saved to disk: " + destFile.getAbsolutePath());

			return screenshotBytes;
		} catch (Exception e) {
			System.out.println("Failed to capture screenshot: " + e.getMessage());
			return new byte[0];
		}
	}

	/**
	 * Captures a screenshot and embeds it directly into the Cucumber HTML report.
	 *
	 * @param scenario Cucumber Scenario instance
	 * @param screenshotName Label for the report attachment
	 */
	public static void captureAndAttachScreenshot(Scenario scenario, String screenshotName) {
		try {
			byte[] screenshotBytes = takeScreenshot(screenshotName);
			if (scenario != null && screenshotBytes != null && screenshotBytes.length > 0) {
				scenario.attach(screenshotBytes, "image/png", screenshotName);
				System.out.println("Screenshot attached to report: " + screenshotName);
			}
		} catch (Exception e) {
			System.out.println("Failed to attach screenshot to scenario: " + e.getMessage());
		}
	}

	public static void tearDown() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}
}
