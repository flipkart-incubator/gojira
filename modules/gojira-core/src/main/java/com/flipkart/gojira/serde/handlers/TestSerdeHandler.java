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
 * <p>
 * //TODO: Add a method to do generics type safe deserialization.
 */
public interface TestSerdeHandler {

  /**
   * @param obj object to be serialized
   * @param <T>
   * @return serialized byte[]
   * @throws TestSerdeException exception thrown if serialization fails
   */
  <T> byte[] serialize(T obj) throws TestSerdeException;

  /**
   * @param bytes serialized byte[] to be de-serialized.
   * @param clazz class to de-serialize
   * @param <T>
   * @return de-serialized object
   * @throws TestSerdeException exception thrown if de-serialization fails
   */
  <T> T deserialize(byte[] bytes, Class<T> clazz) throws TestSerdeException;

  /**
   * @param bytes serialized byte[] to be de-serialized
   * @param obj   object which needs to be updated with the above byte[]
   * @param <T>
   * @throws TestSerdeException exception if we are not able to update
   */
  <T> void deserializeToInstance(byte[] bytes, T obj) throws TestSerdeException;
}
