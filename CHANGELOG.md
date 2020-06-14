## 1.9.7
- Adding sample service as a test.
- Updated jackson-databind dependency to 2.11.0
- Updated guice dependency to 4.2.3 
- Updated maven-shade-plugin dependency to 3.2.4
 
## 1.9.5
- new Mode introduced: DYNAMIC.
- Request level mode support.
- Same machine can be used to run gojira tests as in TEST mode as well as serve normal traffic to the app as in NONE mode.

## 1.9.4
- (De)Serialization changes
- Introduced JsonMapListSerdeHandler that natively supports (de)serialization of any kind of Map-like and List-like objects.
- Refactored the native serdehandlers to make it modular and re-usable.
- Any custom (de)serializer can be added to the native serdehandlers using register(De)Serializer method.
- Clients can now extend the native serdehandlers and modify the objectmapper based on the use case.
- JsonTestSerdeHandler is marked deprecated. The same will be removed in the subsequent updates. Please move to JsonDefaultSerdeHandler and JsonMapListSerdeHandler at the earliest based on your case of use.
- Unit tests are added for better understanding.


## 1.9.3
- Bugfix/methodDataValidationMap.

## 1.9.2
- Added checkstyle format validations.

## 1.9.1
- patch release for aspectJ for removing SoftException usage.

## 1.9.0
- Added aspectJ support.

## 1.8.0
- Added RabbitMQ support.
- HelperConfig removed.
- ExternalConfig will be TestDataType-aware (Http, Kafka, Rmq, etc).

## 1.7.4
- NON_EMPTY_METHOD_DATA_MAP result type added.
- If any methodData is left unconsumed after test execution, NON_EMPTY_METHOD_DATA_MAP shall be the result of the test.

## 1.7.3
- jackson-databind dependency update to 2.9.10.3

## 1.7.2-RELEASE
- Initial Flipkart-Incubator release
