package runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = "stepDefinitions",
		/*--------------- To run specific scenarios, filter by tag, ----------------

		tags = "@PositiveFlow" -> Runs the full positive flow
		tags = "@LoginValidation" -> Runs the login username/password validation check
		tags = "@StepValidations" -> Runs the step-by-step validations after valid login */
		
		tags = "@LoginValidation", plugin = { "pretty", "html:target/cucumber-reports.html", "json:target/cucumber.json" }

)
public class TestRunner {

}
