# Test strategy

What this suite is for, what it covers, and what it leaves alone. Written the way I would
write it for a real product team.

## Scope

The application under test is the SauceDemo storefront: sign in, browse a catalogue, fill
a basket, check out. The suite answers one question on every commit, which is whether a
customer can still buy something, and it answers it fast enough that people wait for it.

## What is automated, and at which level

| Level | What it covers | Why here |
|---|---|---|
| API (RestAssured) | Status codes, payload shape, filtering, error handling | Fast, stable, no browser; catches contract breaks before the UI is opened |
| UI smoke (TestNG) | Sign in, list products, add to cart, buy | The path that must never break; runs on every commit in a couple of minutes |
| UI regression (TestNG) | Sorting, validation, cart mechanics, cancel paths, access control | Runs on pull requests and nightly |
| BDD (Cucumber) | Sign-in rules and the purchase journey, in business language | Only where a non-developer benefits from reading the scenario |

The split follows the usual pyramid logic. Anything that can be asserted below the UI is
asserted below the UI, because a browser test that could have been an API test is a slow
test with more ways to fail.

## What is not automated here, and why

- **Visual appearance.** Pixel comparison belongs in a dedicated visual tool, not in
  assertions about CSS classes that change every release.
- **Load and performance.** The API suite has a coarse response-time budget as an early
  warning. Real performance testing needs a load tool and a controlled environment.
- **The `problem_user` and `performance_glitch_user` accounts.** They exist to make the
  demo site misbehave. Useful for practising debugging, but a test built on deliberately
  broken behaviour asserts nothing about the real product.
- **Payment.** SauceDemo does not take payment, so there is nothing to test.

## Handling flakiness

1. No `Thread.sleep`; explicit waits only.
2. One browser per test, so no test depends on another's leftovers.
3. Retries capped at one and logged, so flakiness stays visible instead of being quietly
   absorbed into a green build.
4. A screenshot is attached to the report on every failure, so the first investigation
   does not start with "run it again and see".

## Entry and exit criteria

- **Entry:** the site is reachable and the smoke suite passes against it.
- **Exit:** the regression suite is green on both browsers in the matrix. Any test that
  needed a retry to pass is raised as a defect against the suite itself.

## Risks

- The suite runs against a public demo site, so an outage or a change there breaks the
  build for reasons that have nothing to do with the code. The nightly run exists to
  catch that and separate it from pull-request failures.
- Locators are tied to the site's `data-test` attributes and class names. Where the site
  offers a `data-test` hook it is used, because those attributes are the least likely to
  change for cosmetic reasons.
