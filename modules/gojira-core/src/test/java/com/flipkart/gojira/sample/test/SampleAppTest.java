/*
 * Copyright 2020 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.gojira.sample.test;

import com.flipkart.gojira.core.TestExecutionModule;
import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.core.injectors.TestExecutionInjector;
import com.flipkart.gojira.execute.TestExecutor;
import com.flipkart.gojira.execute.TestExecutorModule;
import com.flipkart.gojira.external.Managed;
import com.flipkart.gojira.external.ManagedModule;
import com.flipkart.gojira.external.config.ExternalConfig;
import com.flipkart.gojira.external.config.HttpConfig;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.http.HttpTestDataType;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.sample.app.SampleApp;
import com.flipkart.gojira.sample.app.http.SampleAppHttpHelper;
import com.flipkart.gojira.sample.app.module.SampleAppDI;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.config.DataStoreModule;
import com.flipkart.gojira.sinkstore.file.FileBasedDataStoreHandler;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author narendra.vardi */
public class SampleAppTest {
  @BeforeClass
  public static void setup() throws Exception {
    SampleApp.main(null);
    SampleAppDI.install(new TestExecuteModule());
    SampleAppDI.di().getInstance(Managed.class).setup();
  }

  @AfterClass
  public static void tearDown() {
    GuiceInjector.unAssignInjector();
  }

  @Test
  public void testGethubUserMeta() {
    try {
      new SampleAppHttpHelper(new OkHttpClient())
          .doGet(
              "http://localhost:5000/github/xyz",
              Headers.of(Collections.singletonMap("X-GOJIRA-MODE", "PROFILE")));
      Thread.sleep(10000);
    } catch (Exception e) {
      throw new RuntimeException("test initiation failed.", e);
    }

    String testId = "getGithubUserMeta";
    try {
      TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData =
          (TestData)
              TestExecutionInjector.getInjector()
                  .getInstance(SerdeHandlerRepository.class)
                  .getTestDataSerdeHandler()
                  .deserialize(
                      SampleAppDI.di().getInstance(SinkHandler.class).read(testId), TestData.class);
      SampleAppDI.di()
          .getInstance(Key.get(TestExecutor.class, Names.named("HTTP")))
          .execute(testData, "DEFAULT");
      Thread.sleep(5000);
    } catch (Exception e) {
      throw new RuntimeException("test execution failed.", e);
    }

    String result = null;
    try {
      result = new String(SampleAppDI.di().getInstance(SinkHandler.class).read(testId));
    } catch (Exception e) {
      throw new RuntimeException("test reading results failed.", e);
    }

    Assert.assertEquals("SUCCESS", result);
  }

  @Test
  public void testPostHttpBinData() {
    try {
      new SampleAppHttpHelper(new OkHttpClient())
          .doPost(
              "http://localhost:5000/httpbin/post",
              "",
              Headers.of(Collections.singletonMap("X-GOJIRA-MODE", "PROFILE")));
      Thread.sleep(10000);
    } catch (Exception e) {
      throw new RuntimeException("test initiation failed.", e);
    }
    String testId = "postHttpBinData";
    try {
      TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData =
          (TestData)
              TestExecutionInjector.getInjector()
                  .getInstance(SerdeHandlerRepository.class)
                  .getTestDataSerdeHandler()
                  .deserialize(
                      SampleAppDI.di().getInstance(SinkHandler.class).read(testId), TestData.class);
      SampleAppDI.di()
          .getInstance(Key.get(TestExecutor.class, Names.named("HTTP")))
          .execute(testData, "DEFAULT");
      Thread.sleep(5000);
    } catch (Exception e) {
      throw new RuntimeException("test execution failed.", e);
    }

    String result = null;
    try {
      result = new String(SampleAppDI.di().getInstance(SinkHandler.class).read(testId));
    } catch (Exception e) {
      throw new RuntimeException("test reading results failed.", e);
    }

    Assert.assertEquals("COMPARE_FAILED", result);
  }

  private static class TestExecuteModule extends AbstractModule {
    @Override
    protected void configure() {
      DataStoreConfig dataStoreConfig =
          DataStoreConfig.builder()
              .setDataStoreHandler(new FileBasedDataStoreHandler("/tmp/datastore"))
              .build();
      install(new DataStoreModule(dataStoreConfig));

      Map<String, List<ExternalConfig>> externalConfigMap;
      externalConfigMap = Maps.newHashMap();
      List<ExternalConfig> externalConfigs = new ArrayList<>();
      HttpConfig externalConfig = new HttpConfig();
      externalConfig.setConnectionTimeout(5000);
      externalConfig.setHostNamePort("localhost:5000");
      externalConfig.setMaxConnections(50);
      externalConfig.setOperationTimeout(60000);
      externalConfigs.add(externalConfig);
      externalConfigMap.put("DEFAULT", externalConfigs);

      install(new TestExecutionModule(externalConfigMap));
      install(new ManagedModule());
      install(new TestExecutorModule());
    }
  }
}
