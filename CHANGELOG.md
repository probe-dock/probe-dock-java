## v0.4.0 - March 8, 2016

* Added the support to send SCM data to Probe Dock. You can set several env vars to send the SCM data:
    - PROBEDOCK_SCM_NAME
    - PROBEDOCK_SCM_VERSION
    - PROBEDOCK_SCM_BRANCH
    - PROBEDOCK_SCM_COMMIT
    - PROBEDOCK_SCM_DIRTY
    - PROBEDOCK_SCM_REMOTE_NAME
    - PROBEDOCK_SCM_REMOTE_URL_FETCH
    - PROBEDOCK_SCM_REMOTE_URL_PUSH
    - PROBEDOCK_SCM_REMOTE_AHEAD
    - PROBEDOCK_SCM_REMOTE_BEHIND
* Added the support to accept configuration file through PROBEDOOCK_CONFIG env var
* Added the support to configure the test base path through PROBEDOCK_TESTBASEPATH env var

## v0.3.1 - November 9, 2015

* Fixed a possible NPE related to https://github.com/probedock/probedock-junit/issues/1

## v0.3.0 - October 28, 2015

* Added the support to specify the category based on package patterns configured in `probedock.yml`

  ```yml
  java:
    categoriesByPackage:
      io.probedock.integration.*: Integration
      io.probedock.rest.*: API
  ```

## v0.2.2 - August 31, 2015

* Fixed PROBE_DOCK to PROBEDOCK for env vars prefix

## v0.2.0 - June 9, 2015

* **Breaking changes**
* Added contributors in test (through @ProbeTest and @ProbeTestClass)
* Added context in test run to add several execution context like memory usage, java versions, OS info
* Added probe info in test run to have the name and version of the probe
* Added fingerprint (calculated on package.class.method) in test to identify each test not based on the test name
* Totally removed the caching mechanism inherited from long time ago and not relevant anymore
* Replaced logging framework by java.logging (removed dependencies)
* Added pipeline and stage to test run to identify a stage in a pipeline (added through project configuration)

## v0.1.1 - v0.1.3 - May 28, 2015 - June 01, 2015

* Several bug fixes
* Taking care correctly of the test run uid

## v0.1.0 - May 18, 2015

* Refactoring of the old Java base client.
