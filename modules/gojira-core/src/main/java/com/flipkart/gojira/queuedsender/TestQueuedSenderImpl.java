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

package com.flipkart.gojira.queuedsender;

import com.flipkart.gojira.core.injectors.GuiceInjector;
import com.flipkart.gojira.models.TestData;
import com.flipkart.gojira.models.TestDataType;
import com.flipkart.gojira.models.TestRequestData;
import com.flipkart.gojira.models.TestResponseData;
import com.flipkart.gojira.serde.SerdeHandlerRepository;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;
import com.leansoft.bigqueue.BigQueueImpl;
import com.leansoft.bigqueue.IBigQueue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for {@link TestQueuedSender}
 */
public class TestQueuedSenderImpl extends TestQueuedSender {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(TestQueuedSenderImpl.class.getSimpleName());
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
  private IBigQueue messageQueue;
  ;

  public void setup() throws Exception {
    Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
    FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
    Files.createDirectories(Paths.get(testQueuedSenderConfig.getPath()), attr);
    this.messageQueue = new BigQueueImpl(testQueuedSenderConfig.getPath(), "gojira-messages");

    MessageSenderThread messageSenderThread = new MessageSenderThread(messageQueue);
    scheduler.scheduleWithFixedDelay(messageSenderThread, 20,
        testQueuedSenderConfig.getQueuePurgeInterval(), TimeUnit.SECONDS);
    scheduler.scheduleAtFixedRate(new TestQueueCleaner(messageQueue), 20,
        testQueuedSenderConfig.getQueuePurgeInterval(), TimeUnit.SECONDS);
  }

  public void shutdown() throws Exception {
    while (!messageQueue.isEmpty()) {
      Thread.sleep(1000);
    }
    this.scheduler.shutdownNow();
  }

  public <T extends TestDataType> void send(
      TestData<TestRequestData<T>, TestResponseData<T>, T> testData) throws Exception {
    if (messageQueue.size() < testQueuedSenderConfig.getQueueSize()) {
      LOGGER.info("TestData with id: " + testData.getId() + " enqueued.");
      messageQueue.enqueue(GuiceInjector.getInjector().getInstance(SerdeHandlerRepository.class)
          .getTestDataSerdeHandler().serialize(testData));
    } else {
      LOGGER.error("messageQueue size greater than " + testQueuedSenderConfig.getQueueSize()
          + " testData.id " + testData.getId());
    }
  }

  private static final class MessageSenderThread implements Runnable {

    private IBigQueue messageQueue;

    public MessageSenderThread(IBigQueue messageQueue) {
      this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
      try {
        while (!messageQueue.isEmpty()) {
          LOGGER.info("There are messages in the hyperion message queue. Sender invoked.");
          byte[] data = messageQueue.dequeue();
          if (null == data) {
            break;
          }
          TestData<TestRequestData<TestDataType>, TestResponseData<TestDataType>, TestDataType> testData = GuiceInjector
              .getInjector().getInstance(SerdeHandlerRepository.class)
              .getTestDataSerdeHandler().deserialize(data, TestData.class);
          LOGGER.info("TestData with id: " + testData.getId() + " send for DataStore write.");
          GuiceInjector.getInjector().getInstance(SinkHandler.class).write(testData.getId(), data);
        }
      } catch (Exception e) {
        LOGGER.error("Could not send message: ", e);
      }
    }
  }

  private static final class TestQueueCleaner implements Runnable {

    private IBigQueue messageQueue;

    private TestQueueCleaner(IBigQueue messageQueue) {
      this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
      try {
        long startTime = System.currentTimeMillis();
        this.messageQueue.gc();
        LOGGER.info(String.format("Ran GC on queue. Took: %d milliseconds",
            (System.currentTimeMillis() - startTime)));
      } catch (IOException e) {
        LOGGER.error("Could not perform GC on hyperion message queue: ", e);
      }
    }
  }
}
