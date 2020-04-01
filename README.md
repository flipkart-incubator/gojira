# Gojira[![Travis build status](https://travis-ci.org/flipkart-incubator/gojira.svg?branch=master)](https://travis-ci.org/flipkart-incubator/gojira) 
Gojira is a record and replay based regression testing tool for Java services. A well tested framework that is being used by `order management systems` at Flipkart.  

## Features
1. Record and Replay framework for single request-response scope executions, that goes beyond just recording http request and response data, by additionally enabling recording of any call, external to the jvm, and storing them against a single test-id.
2. Start your JVM in 4 modes: PROFILE(for recording), TEST(when replaying), NONE and SERIALIZE(test de-serialization of recorded data).
3. javax.servlet based Filter for capturing HTTP request(uri, headers, method, body, queryparams) and response(status code, headers, body) data.
4. Request sampling capabilities based on URI signature and time-based sampling.
5. Annotation based method interception support with Guice to capture method data - arguments before and after method execution, and return or exception data.
6. Custom serialization handlers, compare handlers and hash handlers per method argument and return or exception data.
7. Intermediate storage during recording in a BigQueue before flushing to data-store.
6. Interfaces to plug-in data-store for storing recorded data.
8. Test executors for running tests in replay mode.
9. Very low overhead during NONE and PROFILE mode. TODO: Add metrics.

## Basic Terms:

1. HttpFilter - Request Filter which helps to capture request, response and headers related to that request. 
2. BindingsModule - This module is used for setting up method interceptors alone which uses `Google Guice AOP`
3. SetupModule -  This module is used for setting up all the requirements of Gojira. The fields that are required for SetupModule installation
   1. RequestSamplingConfig
      a. Whitelist - URLs that needs to be whitelisted for gojira profiling and testing.
      b. SamplingPercentage - Percentage of requests that needs to be profiled. 
   2. SerdeConfig
      a. DefaultSerdeHandler - This is used for serialising and deserialising of data profiled by Gojira. 
   3. GojiraComparisonConfig
      a. DiffIgnoreMap - This gives the client to ignore certain fields. For example, If a field is storing `timestamp` just for audit purposes, we can consider ignoring these during tests.
      b. DefaultCompareHandler - This handler helps with comparing the profiled data and test data. Default compare handler is `JsonTestCompareHandler`
      c. ResponseDataCompareHandler - This handler helps with comparing the response profiled data and response test data. If not provided, `JsonTestCompareHandler` is used. 
   4. DataStoreConfig - Helps to bind `SinkHandler` with implementation.
   5. TestQueuedSenderConfig
      a. Path - Disk path in the box where the data needs to be stored temporarily before pushing it to DB.
      b. QueueSize - Configure MaxQueue size so that your disk space usage can be limited.


## Config Details: 




## Changelog
[Changelog](https://github.com/flipkart-incubator/gojira/blob/initial-commit/CHANGELOG.md)

## Getting Started
TODO: Add a simple javax.servlet example. 

## Users
[Flipkart](http://www.flipkart.com)

## Contribution, Bugs and Feedback
For bugs, questions and discussions, please use [Github Issues](https://github.com/flipkart-incubator/gojira/issues).

For contributions, please check [Contributions](https://github.com/flipkart-incubator/gojira/blob/initial-commit/CONTRIBUTING.md)

## License
Copyright 2020 Flipkart Internet, pvt ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
