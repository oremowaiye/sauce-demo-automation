# SauceDemo Test Automation Framework

A Selenium 4 + Java test automation framework built against [saucedemo.com](https://www.saucedemo.com),
with a Cucumber BDD layer, RestAssured API checks, parallel cross-browser execution and CI.

Built by **Ore Mowaiye** as a working example of how I structure an automation suite:
page objects with no waits leaking into the tests, configuration that is the same locally
and in CI, and failures that explain themselves without a rerun.

---

## What this demonstrates

| Area | How it shows up here |
|---|---|
| Selenium 4 | Page Object Model, explicit waits only, Selenium Manager for driver binaries |
| Java | Records, enums, streams, `ThreadLocal` driver management, Java 17 |
| Test design | Smoke vs regression suites, data-driven negative cases, one browser per test |
| Parallel execution | TestNG `parallel="classes"`, thread-safe driver factory |
| Cross-browser | Chrome, Firefox and Edge from one switch; CI runs Chrome and Firefox in a matrix |
| BDD | Cucumber feature files sharing the same page objects, with PicoContainer DI |
| API testing | RestAssured with a shared request spec, negative cases and a response-time budget |
| Reporting | Allure (steps, severities, screenshots on failure), Cucumber HTML, Surefire |
| CI | GitHub Actions on push, PR and nightly, with the Allure report published to Pages |

---

## Running it

**Prerequisites:** JDK 17+, Maven 3.9+, and Chrome or Firefox installed. Driver binaries
are resolved automatically by Selenium Manager - there is nothing to download by hand.

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

To run a single class or method, use your IDE's TestNG runner - the Maven build is
driven by the suite XML files rather than by `-Dtest`.

### Reports

```bash
mvn allure:serve        # opens the Allure report in a browser
```

Everything else lands under `target/`: `surefire-reports/` for the raw TestNG output,
`cucumber/cucumber-report.html` for the Gherkin run, and `screenshots/` for the PNG of
any failure.

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

### Decisions worth explaining at interview

**No `Thread.sleep`, anywhere.** Every interaction goes through the explicit-wait helpers
on `BasePage`. A fixed sleep is either too short (flaky) or too long (slow), and usually
both in the same suite.

**One browser per test method, not per class.** It costs a few seconds per test and buys
complete isolation: no test can pass or fail because of what another one left behind.
That is what makes parallel execution safe rather than merely fast.

**The driver lives in a `ThreadLocal`.** With `parallel="classes"`, TestNG hands each class
its own thread; the factory hands each thread its own browser. Nothing is shared, so
nothing has to be synchronised.

**Configuration resolves system property first, file second.** The same command runs
headless in CI and visible locally, with no branching in the test code and no second
config file to keep in step.

**Page objects return page objects.** `cart.checkout().submit(details).finish()` reads like
the journey it tests, and a navigation that lands somewhere unexpected fails at the page
object rather than three assertions later.

**Retries are capped at one, deliberately.** They cover genuine infrastructure flakiness.
A test that only passes on the second attempt is a bug report, not a green build - so the
retry is logged loudly in the console output.

**Assertions carry a message.** Every assertion says what was expected and what was found,
because the first thing anyone does with a red CI run is read the failure line.

---

## Coverage

Roughly 30 UI checks plus 7 API checks across:

- **Authentication** - valid sign-in, locked-out account, six invalid credential
  combinations, error dismissal, sign-out, and a broken-access-control check that the
  inventory page cannot be reached without a session
- **Catalogue** - product listing integrity, all four sort orders, details page parity
  with the listing, add and remove from both the listing and the details page
- **Cart** - badge counts, quantities, removal, persistence across navigation, reset state
- **Checkout** - the end-to-end purchase, summary parity with the cart, the total
  arithmetic (item total + tax), field validation, and both cancel paths
- **API** - read, collection shape, create, filtering, 404 handling, response-time budget

See [`docs/test-strategy.md`](docs/test-strategy.md) for what is deliberately *not*
automated here, and why.

---

## Continuous integration

`.github/workflows/ci.yml` runs on every push and pull request to `main`, nightly at
02:00 UTC, and on demand:

- the API suite (no browser, finishes in seconds)
- the UI regression suite in a **Chrome and Firefox matrix**
- the Cucumber scenarios
- failure screenshots, Surefire output and Allure results uploaded as artifacts
- the merged Allure report published to GitHub Pages, with history kept across runs

---

## Notes

SauceDemo is a public demo site maintained by Sauce Labs for exactly this purpose. The
credentials in `config.properties` are the site's published demo accounts, not secrets.
