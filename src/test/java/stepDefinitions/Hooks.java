package stepDefinitions;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import base.TestBase;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.ScreenRecorderUtil;

public class Hooks extends TestBase {

	@Before
	public void beforeScenario(Scenario scenario) {
		ScreenRecorderUtil.startRecord(scenario.getName());
	}

	/**
	 * Runs after every single Gherkin step (Given, When, Then, And)
	 * and attaches a screenshot directly under that step in cucumber-reports.html
	 */
	@AfterStep
	public void attachScreenshotAfterEachStep(Scenario scenario) {
		if (driver != null) {
			try {
				byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
				scenario.attach(screenshot, "image/png", "Step Screenshot");
			} catch (Exception e) {
				System.err.println("Failed to capture step screenshot: " + e.getMessage());
			}
		}
	}

	@After
	public void tearDown(Scenario scenario) {
		try {
			if (driver != null) {
				if (scenario.isFailed()) {
					System.out.println("Scenario failed. Capturing failure screenshot for report...");
					captureAndAttachScreenshot(scenario, "FAILURE - " + scenario.getName());
				} else {
					System.out.println("Scenario passed. Capturing success screenshot for report...");
					captureAndAttachScreenshot(scenario, "SUCCESS - " + scenario.getName());
				}
			}
		} finally {
			ScreenRecorderUtil.stopRecord();
			TestBase.tearDown();
		}
	}
}
