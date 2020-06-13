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

import static com.flipkart.gojira.core.GlobalConstants.COMPARE_FAILED;
import static com.flipkart.gojira.core.GlobalConstants.RESULT_SUCCESS;

import com.flipkart.gojira.core.GlobalConstants;
import com.flipkart.gojira.core.Mode;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for demo-ing and checking a Dropwizard {@link SampleApp} which is integrated with
 * gojira by testing a couple of APIs first in {@link com.flipkart.gojira.core.Mode#PROFILE} and
 * then in {@link com.flipkart.gojira.core.Mode#TEST}.
 */
public class SampleAppTest {
  // file data-store path
  private static final String DATASTORE_FILE_PATH = "/tmp/datastore";

  // big-queue properties
  private static final String BIG_QUEUE_MESSAGE_DIR = "/tmp/gojira-messages/";

  // external-config parameters
  private static final String SAMPLE_APP_HOST_PORT = "localhost:5000";
  private static final int TIMEOUT_IN_MS = 5000;
  private static final int NUM_CONNECTIONS = 5;

  // uris
  private static final String GET_GITHUB_USER_META_URL = "http://" + SAMPLE_APP_HOST_PORT + "/github/usersFlipkartIncubator";
  private static final String POST_HTTPBIN_DATA_URL = "http://" + SAMPLE_APP_HOST_PORT + "/httpbin/post";

  // wait durations
  private static final long WAIT_DURATION_IN_MS_AFTER_PROFILE_BEFORE_TEST = 10000;
  private static final long WAIT_DURATION_IN_MS_AFTER_TEST_BEFORE_RESULT  = 5000;

  // default client-id
  private static final String CLIENT_ID = "DEFAULT";

  /**
   * Setup for the test.
   * 1. Start {@link SampleApp}.
   * 2. Install {@link TestExecuteModule} and setup connections.
   *
   * @throws Exception exception if any.
   */
  @BeforeClass
  public static void setup() throws Exception {
    SampleApp.main(null);
    SampleAppDI.install(new TestExecuteModule());
    SampleAppDI.di().getInstance(Managed.class).setup();
  }

  /**
   * Teardown for the test. 1. un-assign injector which would have been called by {@link
   * com.flipkart.gojira.core.SetupModule}.
   * 2. Delete the files created.
   *  a. {@link #DATASTORE_FILE_PATH}
   *  b. {@link #BIG_QUEUE_MESSAGE_DIR}
   * @throws IOException exception if delete fails
   */
  @AfterClass
  public static void tearDown() throws IOException {
    GuiceInjector.unAssignInjector();
    Files.delete(Paths.get(DATASTORE_FILE_PATH));
    Files.walk(Paths.get(BIG_QUEUE_MESSAGE_DIR))
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
  }

  /**
   * Test a GET API by
   * 1. First making a call in {@link com.flipkart.gojira.core.Mode#PROFILE}.
   * 2. Then initiate the test.
   * 3. Read the result data.
   * 4. Verify that it is successful by checking that result is SUCCESS.
   */
  @Test
  public void testGetGithubUserMeta() {
    try {
      new SampleAppHttpHelper(new OkHttpClient())
          .doGet(
              GET_GITHUB_USER_META_URL,
              Headers
                  .of(Collections.singletonMap(GlobalConstants.MODE_HEADER, Mode.PROFILE.name())));
      Thread.sleep(WAIT_DURATION_IN_MS_AFTER_PROFILE_BEFORE_TEST);
    } catch (Exception e) {
      throw new RuntimeException("test initiation failed.", e);
    }

    String testId = System.nanoTime() + Long.toString(Thread.currentThread().getId());
    try {
      TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData =
          (TestData)
              TestExecutionInjector.getInjector()
                  .getInstance(SerdeHandlerRepository.class)
                  .getTestDataSerdeHandler()
                  .deserialize(
                      SampleAppDI.di().getInstance(SinkHandler.class).read(testId), TestData.class);
      SampleAppDI.di()
          .getInstance(
              Key.get(TestExecutor.class, Names.named(testData.getRequestData().getType())))
          .execute(testData, CLIENT_ID);
      Thread.sleep(WAIT_DURATION_IN_MS_AFTER_TEST_BEFORE_RESULT);
    } catch (Exception e) {
      throw new RuntimeException("test execution failed.", e);
    }

    String result = null;
    try {
      result = new String(SampleAppDI.di().getInstance(SinkHandler.class).read(testId));
    } catch (Exception e) {
      throw new RuntimeException("test reading results failed.", e);
    }

    Assert.assertEquals(RESULT_SUCCESS, result);
  }

  /**
   * Test a POST API by
   * 1. First making a call in {@link com.flipkart.gojira.core.Mode#PROFILE}.
   * 2. Then initiate the test.
   * 3. Read the result data.
   * 4. Verify that it is successful by checking that result is COMPARE_FAILED(intentionally
   * introduced failure).
   */
  @Test
  public void testPostHttpBinData() {
    try {
      new SampleAppHttpHelper(new OkHttpClient())
          .doPost(
              POST_HTTPBIN_DATA_URL,
              "",
              Headers.of(
                  Collections.singletonMap(GlobalConstants.MODE_HEADER, Mode.PROFILE.name())));
      Thread.sleep(WAIT_DURATION_IN_MS_AFTER_PROFILE_BEFORE_TEST);
    } catch (Exception e) {
      throw new RuntimeException("test initiation failed.", e);
    }
    String testId = System.nanoTime() + Long.toString(Thread.currentThread().getId());
    try {
      TestData<HttpTestRequestData, HttpTestResponseData, HttpTestDataType> testData =
          (TestData)
              TestExecutionInjector.getInjector()
                  .getInstance(SerdeHandlerRepository.class)
                  .getTestDataSerdeHandler()
                  .deserialize(
                      SampleAppDI.di().getInstance(SinkHandler.class).read(testId), TestData.class);
      SampleAppDI.di()
          .getInstance(
              Key.get(TestExecutor.class, Names.named(testData.getRequestData().getType())))
          .execute(testData, CLIENT_ID);
      Thread.sleep(WAIT_DURATION_IN_MS_AFTER_TEST_BEFORE_RESULT);
    } catch (Exception e) {
      throw new RuntimeException("test execution failed.", e);
    }

    String result = null;
    try {
      result = new String(SampleAppDI.di().getInstance(SinkHandler.class).read(testId));
    } catch (Exception e) {
      throw new RuntimeException("test reading results failed.", e);
    }

    Assert.assertEquals(COMPARE_FAILED, result);
  }

  /**
   * Module to be installed for running test.
   */
  private static class TestExecuteModule extends AbstractModule {

    /**
     * 1. Install {@link TestExecuteModule}
     * 2. Install {@link ManagedModule}
     * 3. Install {@link TestExecuteModule}
     */
    @Override
    protected void configure() {
      // install this to read test-data and results
      DataStoreConfig dataStoreConfig =
          DataStoreConfig.builder()
              .setDataStoreHandler(new FileBasedDataStoreHandler(DATASTORE_FILE_PATH))
              .build();
      install(new DataStoreModule(dataStoreConfig));

      HttpConfig externalConfig = new HttpConfig();
      externalConfig.setConnectionTimeout(TIMEOUT_IN_MS);
      externalConfig.setHostNamePort(SAMPLE_APP_HOST_PORT);
      externalConfig.setMaxConnections(NUM_CONNECTIONS);
      externalConfig.setOperationTimeout(TIMEOUT_IN_MS);
      List<ExternalConfig> externalConfigs = new ArrayList<>();
      externalConfigs.add(externalConfig);
      Map<String, List<ExternalConfig>> externalConfigMap;
      externalConfigMap = Maps.newHashMap();
      externalConfigMap.put(CLIENT_ID, externalConfigs);
      install(new TestExecutionModule(externalConfigMap));

      install(new ManagedModule());

      install(new TestExecutorModule());
    }
  }
}
