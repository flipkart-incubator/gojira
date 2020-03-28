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

package com.flipkart.gojira.sinkstore.handlers;

import com.flipkart.gojira.sinkstore.SinkException;

/**
 * Interface to read/write test data and results.
 */
public abstract class SinkHandler {

  public abstract void write(String testId, byte[] testData) throws SinkException;

  public abstract byte[] read(String testId) throws SinkException;

  public abstract void writeResults(String testId, String result) throws SinkException;

  //TODO: Add method for reading results.
}