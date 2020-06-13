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

package com.flipkart.gojira.sinkstore.file;

import com.flipkart.gojira.sinkstore.SinkException;
import com.flipkart.gojira.sinkstore.handlers.SinkHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Sample implementation of {@link SinkHandler}.
 */
public class FileBasedDataStoreHandler extends SinkHandler {

  /**
   * Path to the file where data is read from and written to.
   */
  private Path file;

  public FileBasedDataStoreHandler(String path) {
    this.file = Paths.get(path);
  }

  @Override
  public void write(String id, byte[] testData) throws SinkException {
    try {
      Files.write(file, testData);
    } catch (Exception e) {
      throw new SinkException();
    }
  }

  @Override
  public byte[] read(String id) throws SinkException {
    try {
      return Files.readAllBytes(file);
    } catch (Exception e) {
      throw new SinkException();
    }
  }

  @Override
  public void writeResults(String id, String result) throws SinkException {
    try {
      Files.write(file, result.getBytes());
    } catch (IOException e) {
      throw new SinkException();
    }
  }
}
