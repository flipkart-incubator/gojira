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

package com.flipkart.gojira.core;

import static com.flipkart.gojira.core.DI.di;
import static com.flipkart.gojira.core.GlobalConstants.RESULT_SUCCESS;

import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.flipkart.gojira.requestsampling.config.RequestSamplingConfig;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.flipkart.gojira.serde.handlers.json.JsonMapListSerdeHandler;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.config.DataStoreModule;
import com.flipkart.gojira.sinkstore.file.FileBasedDataStoreHandler;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;
import com.google.inject.AbstractModule;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class to simulate and test parallel executions on a single method in a single request-response
 * scope.
 */
public class ParallelCallTest {
  private static ParallelCallThreadPoolExecutor executor;

  // executor params.
  // keeping number of threads very high to enable maximum parallelism.
  private static final int NUMBER_OF_THREADS = 500;
  private static final int KEEP_ALIVE_TIME = 120;
  private static final int BLOCKING_QUEUE_SIZE = 500;

  // wait time before ending profiling or testing.
  private static final int WAIT_TIME_IN_MS_BEFORE_ENDING_PROFILING = 20000;

  // wait time before ending profiling or testing.
  private static final int WAIT_TIME_IN_MS_BEFORE_ENDING_TESTING   = 90000;

  // wait time between end of profiling and start of testing
  private static final int WAIT_TIME_IN_MS_BETWEEN_TEST_START_AND_PROFILING_END = 5000;

  // test-id
  private static final String TEST_ID =
      Long.toString(Thread.currentThread().getId() + System.nanoTime());

  // file data-store path
  private static final String DATASTORE_FILE_PATH = "/tmp/datastore";

  // big-queue properties
  private static final String BIG_QUEUE_MESSAGE_DIR = "/tmp/gojira-messages/";
  private static final long BIG_QUEUE_SIZE = 100L;
  private static final int  BIG_QUEUE_PURGE_INTERVAL_IN_SECONDS = 1;

  // sampling properties
  private static final int SAMPLING_PERCENTAGE = 100;

  /** Class setup. */
  @BeforeClass
  public static void setup() {
    executor =
        new ParallelCallThreadPoolExecutor(
            NUMBER_OF_THREADS, NUMBER_OF_THREADS, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
            BLOCKING_QUEUE_SIZE));
    DI.install(new ParallelCallTestModule());
  }

  /**
   * Simulates calling a method which is intercepted by passing values from 0-99 in random order
   * twice, first in {@link Mode#PROFILE} and then in {@link Mode#TEST}. If all 200 invocations in
   * {@link Mode#TEST} following {@link Mode#PROFILE} are successful, then the test is a success.
   *
   * @throws Exception exception thrown
   */
  @Test
  public void test() throws Exception {
    DefaultProfileOrTestHandler.start(TEST_ID, HttpTestRequestData.builder().build(), Mode.PROFILE);
    randomizeAndExecuteTask();
    randomizeAndExecuteTask();
    Thread.sleep(WAIT_TIME_IN_MS_BEFORE_ENDING_PROFILING);
    DefaultProfileOrTestHandler.end(HttpTestResponseData.builder().build());

    Thread.sleep(WAIT_TIME_IN_MS_BETWEEN_TEST_START_AND_PROFILING_END);

    DefaultProfileOrTestHandler.start(TEST_ID, HttpTestRequestData.builder().build(), Mode.TEST);
    randomizeAndExecuteTask();
    randomizeAndExecuteTask();
    Thread.sleep(WAIT_TIME_IN_MS_BEFORE_ENDING_TESTING);
    DefaultProfileOrTestHandler.end(HttpTestResponseData.builder().build());

    Assert.assertEquals(RESULT_SUCCESS,
        new String(DI.di().getInstance(SinkHandler.class).read(TEST_ID)));
  }

  /**
   * Chooses a value between 0 and 99 randomly and submits {@link CustomRunnable} to {@link
   * ParallelCallThreadPoolExecutor}. Adds the generated number to usedList to re-using the same
   * number again. Returns when the usedList size is 100.
   */
  private void randomizeAndExecuteTask() {
    List<Long> usedList = new ArrayList<>();
    while (true) {
      long num = System.nanoTime() % 100;
      if (!usedList.contains(num)) {
        executor.execute(new CustomRunnable(ProfileRepository.getGlobalPerRequestID(), num));
        usedList.add(num);
      }
      if (usedList.size() == 100) {
        return;
      }
    }
  }

  /**
   * CustomRunnable class that has the following parameters, testId for transferring
   * globalPerRequestId to the another thread and the value used for doing some work.
   */
  private static class CustomRunnable implements Runnable {
    private String testId;
    private long value;

    public CustomRunnable(String testId, long value) {
      this.testId = testId;
      this.value = value;
    }

    /**
     * The run method calls {@link MethodInterceptionTest#checkMethodInterception(Long)} by passing
     * {@link #value} as the variable will be passed in {@link Mode#PROFILE} and {@link Mode#TEST},
     * but in random order to simulate concurrent execution on a single method will different and
     * same values.
     */
    @Override
    public void run() {
      Assert.assertEquals(Long.toString(value), di().getInstance(MethodInterceptionTest.class)
          .checkMethodInterception(value));
    }
  }

  /**
   * Extends {@link ThreadPoolExecutor} to override beforeExecute and afterExecute methods.
   */
  private static class ParallelCallThreadPoolExecutor extends ThreadPoolExecutor {
    public ParallelCallThreadPoolExecutor(
        int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * Sets {@link ProfileRepository#setGlobalPerRequestID(String)} by getting the value from {@link
     * CustomRunnable#value}.
     *
     * @param t the thread that will run task {@code r}
     * @param r the task that will be executed
     */
    public void beforeExecute(Thread t, Runnable r) {
      ProfileRepository.setGlobalPerRequestID(((CustomRunnable) r).testId);
      super.beforeExecute(t, r);
    }

    /**
     * Invokes {@link ProfileRepository#clearGlobalPerRequestID()}.
     *
     * @param r the runnable that has completed
     * @param t the exception that caused termination, or null if execution completed normally
     */
    public void afterExecute(Runnable r, Throwable t) {
      super.afterExecute(r, t);
      ProfileRepository.clearGlobalPerRequestID();
    }
  }

  /**
   * 1. un-assign the injector for other tests to use if required. 2. Delete the files created. a.
   * /tmp/datastore b. /tmp/gojira-messages
   *
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
   * Module to be installed before running tests.
   */
  private static class ParallelCallTestModule extends AbstractModule {

    /**
     * 1. Install {@link SetupModule} to take care of app-config.
     * 2. Install {@link BindingsModule} to take care of app-method-interception.
     * 3. Install {@link DataStoreModule} to read results for verifying.
     */
    @Override
    protected void configure() {
      DataStoreConfig dataStoreConfig =
          DataStoreConfig.builder()
              .setDataStoreHandler(
                  new FileBasedDataStoreHandler(DATASTORE_FILE_PATH))
              .build();

      RequestSamplingConfig requestSamplingConfig =
          RequestSamplingConfig.builder().setSamplingPercentage(SAMPLING_PERCENTAGE).build();

      SerdeConfig serdeConfig =
          SerdeConfig.builder().setDefaultSerdeHandler(new JsonMapListSerdeHandler()).build();

      GojiraComparisonConfig comparisonConfig =
          GojiraComparisonConfig.builder()
              .setDiffIgnoreMap(new HashMap<>())
              .setDefaultCompareHandler(new JsonTestCompareHandler())
              .setResponseDataCompareHandler(new JsonTestCompareHandler())
              .build();

      TestQueuedSenderConfig testQueuedSenderConfig =
          TestQueuedSenderConfig.builder()
              .setPath(BIG_QUEUE_MESSAGE_DIR)
              .setQueueSize(BIG_QUEUE_SIZE)
              .setQueuePurgeIntervalInSeconds(BIG_QUEUE_PURGE_INTERVAL_IN_SECONDS)
              .build();

      SetupModule setupModule =
          new SetupModule(
              Mode.DYNAMIC,
              requestSamplingConfig,
              serdeConfig,
              comparisonConfig,
              dataStoreConfig, // this is required to write test-data and results
              testQueuedSenderConfig);

      install(setupModule);

      // this is required to read test-data and results.
      install(new DataStoreModule(dataStoreConfig));

      // this is to enable method interception.
      install(new BindingsModule());
    }
  }
}
