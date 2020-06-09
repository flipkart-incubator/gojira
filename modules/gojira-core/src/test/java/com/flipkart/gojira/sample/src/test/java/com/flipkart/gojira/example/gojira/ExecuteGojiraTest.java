package com.flipkart.gojira.sample.src.test.java.com.flipkart.gojira.example.gojira;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.execute.TestExecutionException;
import com.flipkart.gojira.execute.TestExecutor;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module.DI;
import com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module.ServiceSampleModule;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.serde.TestSerdeException;
import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import com.flipkart.gojira.sinkstore.SinkException;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import static com.flipkart.gojira.sample.src.main.java.com.flipkart.gojira.example.module.DI.di;


/**
 * @author narendra.vardi
 */
public class ExecuteGojiraTest {
    private TestExecutor<TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType>> testExecutor;

    private SinkHandler sinkHandler = (SinkHandler) GuiceInjector.getInjector().getInstance(SinkHandler.class);
    private TestSerdeHandler testDataSerdeHandler = ((SerdeHandlerRepository) GuiceInjector.getInjector().getInstance(SerdeHandlerRepository.class)).getTestDataSerdeHandler();


    @Inject

    public ExecuteGojiraTest(@Named("HTTP") TestExecutor testExecutor) {
        this.testExecutor = testExecutor;
    }

    public static void main(String[] args) throws SinkException, TestSerdeException, TestExecutionException {
        DI.install(new ServiceSampleModule());
        String testId = "21273198681810781";
        ExecuteGojiraTest executeGojiraTest = di().getInstance(ExecuteGojiraTest.class);
        TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData = (TestData) executeGojiraTest.testDataSerdeHandler.deserialize(executeGojiraTest.sinkHandler.read(testId), TestData.class);
        di().getInstance(ExecuteGojiraTest.class).testExecutor.execute(testData, "DEFAULT");
    }
}
