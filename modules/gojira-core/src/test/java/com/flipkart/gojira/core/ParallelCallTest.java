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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.compare.handlers.json.JsonTestCompareHandler;
import com.flipkart.gojira.compare.config.GojiraComparisonConfig;
import com.flipkart.gojira.models.http.HttpTestRequestData;
import com.flipkart.gojira.models.http.HttpTestResponseData;
import com.flipkart.gojira.queuedsender.config.TestQueuedSenderConfig;
import com.flipkart.gojira.requestsampling.config.RequestSamplingConfig;
import com.flipkart.gojira.serde.config.SerdeConfig;
import com.flipkart.gojira.serde.handlers.json.JsonTestSerdeHandler;
import com.flipkart.gojira.sinkstore.config.DataStoreConfig;
import com.flipkart.gojira.sinkstore.file.FileBasedDataStoreHandler;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.BeforeClass;

/**
 * Created by arunachalam.s on 10/10/17.
 */
public class ParallelCallTest {

  private ParallelCallThreadPoolExecutor executor = new ParallelCallThreadPoolExecutor(400, 400,
      400, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

  @BeforeClass
  public static void setup() throws Exception {
    Map<String, List<String>> jsonDiffIgnoreMap = null;
    try {
      jsonDiffIgnoreMap = new ObjectMapper()
          .readValue(Files.readAllBytes(Paths.get("/etc/fk-gojira/json_diff_ignore_patterns")),
              HashMap.class);
    } catch (Exception e) {
      System.out.println("unable to load ignore_patterns.");
    }

    RequestSamplingConfig requestSamplingConfig = RequestSamplingConfig.builder()
        .setSamplingPercentage(100)
        .setWhitelist(new ArrayList<>())
        .build();
    SerdeConfig serdeConfig = SerdeConfig.builder()
        .setDefaultSerdeHandler(new JsonTestSerdeHandler()).build();
    GojiraComparisonConfig comparisonConfig = GojiraComparisonConfig.builder()
        .setDiffIgnoreMap(jsonDiffIgnoreMap)
        .setDefaultCompareHandler(new JsonTestCompareHandler())
        .setResponseDataCompareHandler(new JsonTestCompareHandler())
        .build();
    DataStoreConfig dataStoreConfig = DataStoreConfig.builder().setDataStoreHandler(
        new FileBasedDataStoreHandler("/var/log/flipkart/fk-gojira/gojira-data"))
        .build();
    TestQueuedSenderConfig testQueuedSenderConfig = TestQueuedSenderConfig.builder()
        .setPath("/var/log/flipkart/fk-gojira/gojira-messages")
        .setQueueSize(10L)
        .build();

    SetupModule profileOrTestModule = new SetupModule(Mode.PROFILE,
        requestSamplingConfig,
        serdeConfig,
        comparisonConfig,
        dataStoreConfig,
        testQueuedSenderConfig);

    DI.install(profileOrTestModule);
  }

  public static void main(String[] args) throws Exception {
    setup();
    System.out.println("ThreadID: " + Thread.currentThread().getId() + " main: before setting: "
        + ProfileRepository.getGlobalPerRequestID());
    ParallelCallTest parallelCallTestPool = new ParallelCallTest();
    DefaultProfileOrTestHandler.start("1", HttpTestRequestData.builder().build(), Mode.PROFILE);
    ProfileRepository.setTestDataId("1");
    List<Long> usedList1 = new ArrayList<>();
    while (true) {
      long num = System.nanoTime() % 100;
      if (!usedList1.contains(num)) {
        parallelCallTestPool.doSomeTask(num);
        usedList1.add(num);
      }
      if (usedList1.size() == 100) {
        break;
      }
    }

    List<Long> usedList2 = new ArrayList<>();
    while (true) {
      long num = System.nanoTime() % 100;
      if (!usedList2.contains(num)) {
        System.out.println(num);
        parallelCallTestPool.doSomeTask(num);
        usedList2.add(num);
      }
      if (usedList2.size() == 100) {
        break;
      }
    }

    Thread.sleep(20000);
    System.out.println("wait");
//        DefaultProfileOrTestHandler.end(new HttpTestResponseData());
    Thread.sleep(20000);
    ProfileRepository.setMode(Mode.TEST);
//        DefaultProfileOrTestHandler.start("1", new HttpTestRequestData());
    for (int i = 0; i < 100; i++) {
      parallelCallTestPool.doSomeTask(i);
    }

    for (int i = 99; i > -1; i--) {
      parallelCallTestPool.doSomeTask(i);
    }
    Thread.sleep(30000);
    DefaultProfileOrTestHandler.end(HttpTestResponseData.builder().build());
    System.exit(1);
  }

  public void doSomeTask(long i) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        System.out.println(i + ":" + di().getInstance(MethodInterceptionTest.class)
            .checkMethodInterception(i, "a".getBytes(), System.currentTimeMillis(), 1));
      }
    });
  }

  public class ParallelCallThreadPoolExecutor extends ThreadPoolExecutor {

    public ParallelCallThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
        TimeUnit unit, BlockingQueue<Runnable> workQueue) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public void beforeExecute(Thread t, Runnable r) {
      System.out.println(
          "ThreadID: " + Thread.currentThread().getId() + " beforeExecute: before setting: "
              + ProfileRepository.getGlobalPerRequestID());
      ProfileRepository.setGlobalPerRequestID("1");
      System.out.println(
          "ThreadID: " + Thread.currentThread().getId() + " beforeExecute: after setting: "
              + ProfileRepository.getGlobalPerRequestID());
      super.beforeExecute(t, r);
    }

    public void afterExecute(Runnable r, Throwable t) {
      super.afterExecute(r, t);
      System.out.println(
          "ThreadID: " + Thread.currentThread().getId() + " afterExecute: before resetting: "
              + ProfileRepository.getGlobalPerRequestID());
      ProfileRepository.clearGlobalPerRequestID();
      System.out.println(
          "ThreadID: " + Thread.currentThread().getId() + " afterExecute: after resetting: "
              + ProfileRepository.getGlobalPerRequestID());
    }
  }
}
