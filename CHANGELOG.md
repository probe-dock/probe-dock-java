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
