package com.flipkart.gojira.core;

import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.compare.GojiraComparisonModule;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.*;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.serde.SerdeModule;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.flipkart.gojira.serde.handlers.json.JsonTestSerdeHandler;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.config.DataStoreModule;
import com.flipkart.gojira.sinkstore.file.FileBasedDataStoreHandler;
import com.google.inject.Guice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by luv.saxena on 14/05/20.
 */
public class TestStartEndTestHandlerTest {

    private static SerdeConfig serdeConfig = SerdeConfig.builder().setDefaultSerdeHandler(new JsonTestSerdeHandler()).build();
    private static GojiraComparisonConfig gojiraComparisonConfig = GojiraComparisonConfig.builder().build();
    private static DataStoreConfig dataStoreConfig = DataStoreConfig.builder().setDataStoreHandler(new FileBasedDataStoreHandler("")).build();

    static {
        ProfileRepository.setGlobalPerRequestID("12345");
        ProfileRepository.begin("12345");
        GuiceInjector.assignInjector(Guice.createInjector(new SerdeModule(serdeConfig),new GojiraComparisonModule(gojiraComparisonConfig), new DataStoreModule(dataStoreConfig)));
    }

    @Test
    public void testIsMethodDataMapEmptyPass(){
        TestData testData = new TestData<>();
        ConcurrentHashMap<
                String,
                ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>>>
                methodDataMap = new ConcurrentHashMap<>();
        methodDataMap.put("M1",new ConcurrentSkipListMap<>());
        testData.setMethodDataMap(methodDataMap);
        ProfileRepository.setTestData(testData);
        TestStartEndTestHandler testStartEndTestHandler = new TestStartEndTestHandler();
        Assert.assertTrue(testStartEndTestHandler.isMethodDataMapEmpty());
    }

    @Test
    public void testIsMethodDataMapEmptyFail(){
        TestData testData = new TestData<>();
        ConcurrentHashMap<
                String,
                ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>>>
                methodDataMap = new ConcurrentHashMap<>();
        ConcurrentSkipListMap<Long, ConcurrentHashMap<MethodDataType, List<MethodData>>> concurrentSkipListMap = new ConcurrentSkipListMap<>();
        concurrentSkipListMap.put(1L,new ConcurrentHashMap<>());
        methodDataMap.put("M2",concurrentSkipListMap);
        testData.setMethodDataMap(methodDataMap);
        ProfileRepository.setTestData(testData);
        TestStartEndTestHandler testStartEndTestHandler = new TestStartEndTestHandler();
        Assert.assertFalse(testStartEndTestHandler.isMethodDataMapEmpty());
    }
}
