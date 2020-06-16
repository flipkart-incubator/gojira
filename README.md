# Gojira[![Travis build status](https://travis-ci.org/flipkart-incubator/gojira.svg?branch=master)](https://travis-ci.org/flipkart-incubator/gojira) 
Gojira is a record and replay based regression testing tool. 

## Features
1. Record and Replay framework for single request-response scope executions, that goes beyond just recording http request and response data, by additionally enabling recording of any call, external to the jvm, and storing them against a single test-id.
2. Start your JVM in 5 modes: PROFILE(for recording), TEST(when replaying), NONE and SERIALIZE(test de-serialization of recorded data), DYNAMIC(for request level mode setting).
3. javax.servlet based Filter for capturing HTTP request(uri, headers, method, body, queryparams) and response(status code, headers, body) data.
4. Request sampling capabilities based on URI signature and time-based sampling.
5. Annotation based method interception support with Guice to capture method data - arguments before and after method execution, and return or exception data.
6. Custom serialization handlers, compare handlers and hash handlers per method argument and return or exception data.
7. Intermediate storage during recording in a BigQueue before flushing to data-store.
6. Interfaces to plug-in data-store for storing recorded data.
8. Test executors for running tests in replay mode.
9. Very low overhead during NONE and PROFILE mode. TODO: Add metrics.

## Changelog
[Changelog](https://github.com/flipkart-incubator/gojira/blob/master/CHANGELOG.md)

## Getting Started
TODO: Add a simple javax.servlet example. 

## Users
[Flipkart](http://www.flipkart.com)

## Contribution, Bugs and Feedback
For bugs, questions and discussions, please use [Github Issues](https://github.com/flipkart-incubator/gojira/issues).

For contributions, please check [Contributions](https://github.com/flipkart-incubator/gojira/blob/master/CONTRIBUTING.md)

## License
Copyright 2020 Flipkart Internet, pvt ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
