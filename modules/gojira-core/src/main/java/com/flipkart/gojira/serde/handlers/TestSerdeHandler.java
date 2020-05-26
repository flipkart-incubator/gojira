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

package com.flipkart.gojira.serde.handlers;

import com.flipkart.gojira.serde.TestSerdeException;

/**
 * Interface for defining serialization and deserialization handlers.
 */
public interface TestSerdeHandler {

  /**
   * This method will be used to serialize Java Object to byte[].
   *
   * @param obj object to be serialized
   * @return serialized byte[]
   * @throws TestSerdeException if serialization fails
   */
  <T> byte[] serialize(T obj) throws TestSerdeException;

  /**
   * This method will be used to deserialize byte[] to Java Object of given class type.
   *
   * @param bytes serialized byte[] to be de-serialized.
   * @param clazz class to de-serialize
   * @return de-serialized object
   * @throws TestSerdeException if de-serialization fails
   */
  <T> T deserialize(byte[] bytes, Class<T> clazz) throws TestSerdeException;

  /**
   * This method will be used to update a Java Object from byte[].
   *
   * @param bytes serialized byte[] to be de-serialized
   * @param obj object which needs to be updated with the above byte[]
   * @throws TestSerdeException if it is unable to update
   */
  <T> void deserializeToInstance(byte[] bytes, T obj) throws TestSerdeException;
}
