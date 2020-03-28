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

package com.flipkart.gojira.external.http;

import com.flipkart.gojira.core.injectors.TestExecutionInjector;
import com.flipkart.gojira.external.ExternalConfigRepository;
import com.flipkart.gojira.external.Managed;
import com.flipkart.gojira.external.SetupException;
import com.flipkart.gojira.external.ShutdownException;
import com.flipkart.gojira.external.config.ExternalConfig;
import java.util.Map;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IHttpManager} and {@link Managed}
 */
public enum HttpManager implements IHttpManager, Managed {
  HTTP_MANAGER; // TODO: there is a hard dependency in Managed. Need to figure out a way to decouple.
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpManager.class);

  @Override
  public void setup() throws SetupException {
    try {
      for (Map.Entry<String, ExternalConfig> entry : TestExecutionInjector.getInjector()
          .getInstance(ExternalConfigRepository.class).getExternalConfig().entrySet()) {
        clientMap.put(entry.getKey(),
            new DefaultAsyncHttpClient(new DefaultAsyncHttpClientConfig.Builder()
                .setConnectTimeout(entry.getValue().getConnectionTimeout())
                .setMaxConnections(entry.getValue().getMaxConnections())
                .setMaxConnectionsPerHost(entry.getValue().getMaxConnections())
                .setKeepAlive(true)
                .setRequestTimeout(entry.getValue().getOperationTimeout())
                .build()));
      }
    } catch (Exception e) {
      LOGGER.error("error setting up http connections.", e);
      throw new SetupException("error setting up http connections.", e);
    }
  }

  @Override
  public DefaultAsyncHttpClient getClient(String key) {
    return clientMap.get(key);
  }

  @Override
  public void shutdown() throws ShutdownException {
    try {
      for (Map.Entry<String, DefaultAsyncHttpClient> entry : clientMap.entrySet()) {
        entry.getValue().close();
      }
    } catch (Exception e) {
      LOGGER.error("error closing http connections.", e);
      throw new ShutdownException("error closing http connections.", e);
    }
  }
}
