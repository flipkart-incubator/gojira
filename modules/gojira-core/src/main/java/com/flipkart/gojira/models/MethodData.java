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

package com.flipkart.gojira.models;

import com.flipkart.gojira.core.Mode;

/**
 * Represents data that is captured per type of {@link MethodDataType} in {@link Mode#PROFILE} mode.
 * All of the below information are needed to reconstruct data in other execution {@link Mode}
 */
public class MethodData {

  /** Class name to help with deserialization. */
  private String className;

  /** Data serialized as bytes. */
  private byte[] data;

  /** Type of method data. */
  private MethodDataType dataType;

  /**
   * position of method data, applicable in case of {@link MethodDataType#ARGUMENT_AFTER} and {@link
   * MethodDataType#ARGUMENT_BEFORE}
   */
  private int position;

  private MethodData() {}

  public MethodData(MethodDataType dataType, String className, byte[] data, int position) {
    this.dataType = dataType;
    this.className = className;
    this.data = data;
    this.position = position;
  }

  public String getClassName() {
    return className;
  }

  public byte[] getData() {
    return data;
  }

  public MethodDataType getDataType() {
    return dataType;
  }

  public int getPosition() {
    return position;
  }
}
