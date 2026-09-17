# SauceDemo Test Automation Framework

[![Test automation suite](https://github.com/oremowaiye/sauce-demo-automation/actions/workflows/ci.yml/badge.svg)](https://github.com/oremowaiye/sauce-demo-automation/actions/workflows/ci.yml)

A Selenium 4 and Java test automation framework built against [saucedemo.com](https://www.saucedemo.com),
with a Cucumber BDD layer, RestAssured API checks, parallel cross-browser execution and CI.

I built this to show how I put an automation suite together: no waits in the test bodies,
one configuration that works the same locally and in CI, and failures you can diagnose
without rerunning them.

---

## What this covers

| Area | How it shows up here |
|---|---|
| Selenium 4 | Page Object Model, explicit waits only, Selenium Manager for driver binaries |
| Java | Records, enums, streams, `ThreadLocal` driver management, Java 17 |
| Test design | Smoke and regression suites, data-driven negative cases, one browser per test |
| Parallel execution | TestNG `parallel="classes"`, thread-safe driver factory |
| Cross-browser | Chrome, Firefox and Edge from one switch; CI runs Chrome and Firefox in a matrix |
| BDD | Cucumber feature files sharing the same page objects, with PicoContainer DI |
| API testing | RestAssured with a shared request spec, negative cases and a response-time budget |
| Reporting | Allure (steps, severities, screenshots on failure), Cucumber HTML, Surefire |
| CI | GitHub Actions on push, PR and nightly, with the Allure report published to Pages |

---

## Running it

**Prerequisites:** JDK 17+, Maven 3.9+, and Chrome or Firefox installed. Selenium Manager
resolves the driver binaries, so there is nothing to download by hand.

```bash
# Full UI regression, headless Chrome
mvn clean test

# Watch it run in a real browser
mvn test -Dheadless=false

# Just the smoke suite
mvn test -P smoke

# A different browser
mvn test -Dbrowser=firefox

# The BDD scenarios
mvn test -P bdd

# The API suite (no browser started)
mvn test -P api
```

To run a single class or method, use your IDE's TestNG runner. The Maven build is driven
by the suite XML files rather than by `-Dtest`.

### Reports

The latest Allure report from CI is published at
[oremowaiye.github.io/sauce-demo-automation](https://oremowaiye.github.io/sauce-demo-automation/),
with pass/fail per test, timings, severities and a screenshot of anything that failed.

To generate it locally:

```bash
mvn allure:serve        # opens the Allure report in a browser
```

The rest lands under `target/`: `surefire-reports/` for the raw TestNG output,
`cucumber/cucumber-report.html` for the Gherkin run, and `screenshots/` for a PNG of any
failure.

---

## How it is put together

```
src/main/java/com/ore/saucedemo/
├── config/      ConfigReader - properties file, overridable by system property
├── driver/      DriverFactory (ThreadLocal), BrowserType
├── model/       Product, CheckoutInfo, TestUser, SortOption
├── pages/       BasePage + one page object per screen, HeaderComponent
└── utils/       ScreenshotUtils

src/test/java/com/ore/saucedemo/
├── base/        BaseTest - browser lifecycle for every UI test
├── data/        TestDataProvider - the data-driven cases
├── listeners/   TestListener, RetryAnalyzer, RetryTransformer
├── tests/       LoginTests, InventoryTests, CartTests, CheckoutTests
├── bdd/         Cucumber runner, hooks, ScenarioContext, step definitions
└── api/         JsonPlaceholderApiTests

src/test/resources/
├── features/    login.feature, checkout.feature
└── suites/      smoke.xml, regression.xml, bdd.xml, api.xml
```

### Why it is built this way

**There is no `Thread.sleep` anywhere.** Every interaction goes through the explicit-wait
helpers on `BasePage`. A fixed sleep is either too short and flaky or too long and slow,
and in a suite of any size it is usually both.

**One browser per test method, not per class.** It costs a few seconds per test. In return
no test can pass or fail because of what another one left behind, which is what makes
parallel execution safe rather than just fast.

**The driver lives in a `ThreadLocal`.** With `parallel="classes"`, TestNG gives each class
its own thread, and the factory gives each thread its own browser. Nothing is shared, so
nothing needs synchronising.

**Configuration checks the system property first, then the file.** The same command runs
headless in CI and visible locally, with no branching in test code and no second config
file to keep in step.

**Page objects return page objects.** `cart.checkout().submit(details).finish()` reads like
the journey it tests, and a navigation that ends up somewhere unexpected fails in the page
object instead of three assertions later.

**Retries are capped at one.** They cover real infrastructure flakiness. A test that only
passes on the second attempt is a bug report rather than a green build, so every retry is
logged loudly in the console.

**Assertions carry a message.** Each one says what was expected and what turned up,
because the first thing anyone does with a red build is read the failure line.

---

## Coverage

About 30 UI checks and 7 API checks:

- **Authentication** - valid sign-in, locked-out account, six invalid credential
  combinations, error dismissal, sign-out, and a check that the inventory page cannot be
  reached without a session
- **Catalogue** - product listing integrity, all four sort orders, details page matching
  the listing, add and remove from both the listing and the details page
- **Cart** - badge counts, quantities, removal, persistence across navigation, reset state
- **Checkout** - the end-to-end purchase, summary matching the cart, the total arithmetic
  (item total plus tax), field validation, and both cancel paths
- **API** - read, collection shape, create, filtering, 404 handling, response-time budget

[`docs/test-strategy.md`](docs/test-strategy.md) covers what is deliberately left out of
this suite, and why.

---

## Continuous integration

`.github/workflows/ci.yml` runs on every push and pull request to `main`, nightly at
02:00 UTC, and on demand:

- the API suite, which needs no browser and finishes in seconds
- the UI regression suite across a **Chrome and Firefox matrix**
- the Cucumber scenarios
- failure screenshots, Surefire output and Allure results uploaded as artifacts
- the merged Allure report published to GitHub Pages, keeping history across runs

---

## Notes

SauceDemo is a public demo site Sauce Labs maintains for exactly this purpose. The
credentials in `config.properties` are the site's own published demo accounts, not
secrets.
