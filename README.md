# Mustadam E-Commerce Test Automation Framework

An enterprise-grade, BDD (Behavior-Driven Development) test automation framework built using **Java**, **Selenium WebDriver**, **Cucumber BDD**, and **JUnit 4**, following the **Page Object Model (POM)** architecture.

Automates and validates end-to-end shopping workflows, cart management, checkout processes, and login validations on the [Mustadam Shop](https://dev.mustadam.shop) web application.

---

## 📋 Table of Contents
1. [Key Features](#-key-features)
2. [Tech Stack & Dependencies](#-tech-stack--dependencies)
3. [Prerequisites](#-prerequisites)
4. [Project Structure](#-project-structure)
5. [How to Run the Tests](#-how-to-run-the-tests)
   - [Method 1: Running from Eclipse IDE (Recommended)](#method-1-running-from-eclipse-ide-recommended)
   - [Method 2: Running from IntelliJ IDEA](#method-2-running-from-intellij-idea)
   - [Method 3: Running from Command Line (Maven CLI)](#method-3-running-from-command-line-maven-cli)
6. [Scenario Tag Reference](#-scenario-tag-reference)
7. [Reports & Generated Artifacts](#-reports--generated-artifacts)

---

## ✨ Key Features

* **Page Object Model (POM)**: Complete separation between page elements/actions (`pages/`) and business validations (`stepDefinitions/`).
* **Behavior-Driven Development (BDD)**: Readable Gherkin specifications in feature files (`.feature`).
* **Multi-Input Same-Browser Testing**: Validates multiple invalid credentials sequentially within a single browser session without repeated browser restarts.
* **Dual Validation Engine**: Automatically handles both HTML5 browser input validations and dynamic DOM elements (toasts, alerts, feedback messages).
* **Automated Screen Recording**: Records video (`.avi`) of scenario execution via Monte Screen Recorder.
* **Per-Step Screenshot Embedding**: Automatically captures screenshots after every single step and embeds them into the Cucumber HTML report.
* **Robust Synchronization**: Uses dynamic explicit waits (`WebDriverWait`) and stale-element recovery (`safeClick`) for single-page applications.

---

## 🛠 Tech Stack & Dependencies

* **Language**: Java (JDK 11, 17, 21, or 22 compatible)
* **Automation Tool**: Selenium WebDriver `4.25.0` (with built-in Selenium Manager for automatic ChromeDriver management)
* **BDD Framework**: Cucumber Java `7.20.1` & Cucumber JUnit `7.20.1`
* **Test Runner / Assertions**: JUnit 4
* **Build Tool**: Apache Maven
* **Video Capture**: Monte Screen Recorder `0.7.7.0`

---

## ⚙️ Prerequisites

Before running the project, ensure you have:
1. **Java JDK 11 or higher** installed (`java -version`).
2. **Google Chrome** installed (latest version recommended).
3. **Eclipse IDE** (or IntelliJ IDEA) with the **Cucumber Eclipse Plugin** installed.
4. **Apache Maven** installed (if running from CLI).

> **Note**: You do not need to download `chromedriver.exe` manually. Selenium 4's built-in **Selenium Manager** automatically detects your installed Chrome version and configures the matching driver.

---

## 📁 Project Structure

```text
CucumberSeleniumFramwork/
│
├── src/test/java/
│   ├── base/
│   │   └── TestBase.java                # Driver initialization, global waits, screenshot capture, teardown
│   ├── pages/                           # Page Object classes (Locators & user actions)
│   │   ├── LoginPage.java               # Language selection, login inputs, validation error capture
│   │   ├── HomePage.java                # Category tab switching, product extraction, Add-to-Cart
│   │   ├── CartPage.java                # Cart items inspection, quantity/price verification, proceed to checkout
│   │   ├── CheckoutPage.java            # Delivery address verification, payment method selection, place order
│   │   └── ProfilePage.java             # My Profile navigation, logout modal confirmation
│   ├── stepDefinitions/
│   │   ├── LoginSteps.java              # Step definition implementations and assertions
│   │   └── Hooks.java                   # @Before (video start), @AfterStep (screenshot), @After (teardown)
│   ├── runner/
│   │   └── TestRunner.java              # JUnit Cucumber Runner with @CucumberOptions
│   └── utils/
│       └── ScreenRecorderUtil.java      # Video recording utility (Monte Media)
│
├── src/test/resources/
│   └── features/
│       └── cartflow.feature             # Gherkin Feature files (Scenarios & DataTables)
│
├── screenshots/                         # Locally saved .png screenshots with timestamps
├── recordings/                          # Saved full-screen .avi video recordings
├── target/
│   └── cucumber-reports.html            # Interactive Cucumber HTML Execution Report
└── pom.xml                              # Maven configuration and dependencies
```

---

## 🚀 How to Run the Tests

### Method 1: Running from Eclipse IDE (Recommended)

1. Open **Eclipse IDE**.
2. Go to **File** → **Import** → **Existing Maven Projects** → Select this project folder.
3. Wait for Maven dependencies to resolve.
4. Navigate to:
   ```text
   src/test/java/runner/TestRunner.java
   ```
5. **Right-click** on `TestRunner.java` → **Run As** → **JUnit Test**.
6. The browser will launch automatically, execute the scenario, and close when complete.

---

### Method 2: Running from IntelliJ IDEA

1. Open **IntelliJ IDEA** → **Open** → Select the project folder.
2. Allow IntelliJ to sync the Maven project.
3. Navigate to `src/test/java/runner/TestRunner.java`.
4. Right-click inside `TestRunner.java` and click **Run 'TestRunner'**.

---

### Method 3: Running from Command Line (Maven CLI)

Open a terminal or command prompt in the project root directory:

* **Run all scenarios configured in TestRunner**:
  ```bash
  mvn clean test
  ```

* **Run a specific scenario by Tag**:
  ```bash
  # Run the Multi-Input Login Validation scenario
  mvn test -Dcucumber.filter.tags="@LoginValidation"

  # Run the Full End-to-End Positive Purchase Flow
  mvn test -Dcucumber.filter.tags="@FullFlow"

  # Run the Cart & Checkout Step Validations
  mvn test -Dcucumber.filter.tags="@StepValidations"
  ```

---

## 🏷 Scenario Tag Reference

In `src/test/java/runner/TestRunner.java`, you can filter which scenarios execute by modifying the `tags` option:

```java
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "stepDefinitions",
    tags = "@LoginValidation",  // Change tag here to switch scenario
    plugin = { "pretty", "html:target/cucumber-reports.html", "json:target/cucumber.json" }
)
```

| Tag | Scenario Description |
| :--- | :--- |
| `@LoginValidation` | **Same-Browser Multi-Credential Validation**: Launches the browser once and tests multiple invalid email/password combinations sequentially, capturing screenshots and error messages for each. |
| `@FullFlow` / `@PositiveFlow` | **Complete E2E Happy Path**: Valid Login → Language selection → Category tab selection (Sofas) → Add to Cart → Cart verification → Checkout → COD Payment → Place Order → My Profile → Logout. |
| `@StepValidations` | **Cart & Checkout Step Assertions**: Detailed step-by-step validation of item details, bill breakdown, and address verification. |

---

## 📊 Reports & Generated Artifacts

After execution, all test artifacts are automatically generated:

1. **Cucumber HTML Report**:
   * Open `target/cucumber-reports.html` in any browser (Chrome, Edge).
   * Displays step-by-step execution status, durations, error traces, and **embedded inline screenshots**.
2. **Screenshots**:
   * Located in the `screenshots/` directory with scenario name and timestamp (e.g., `Order_Placed_Status_20261030_075841.png`).
3. **Screen Recordings**:
   * Located in the `recordings/` directory with scenario name and timestamp (e.g., `Login_and_add_first_category_product_to_cart_20261030_104738.avi`).
