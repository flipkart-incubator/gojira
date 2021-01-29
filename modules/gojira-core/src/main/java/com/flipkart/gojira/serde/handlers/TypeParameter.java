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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Represents a generic type {@code T}. This class will be used to get the type information at
 * runtime by sub-classing and is a work-around for type erasure in Java. Reference taken from
 * FasterXML TypeReference and Google Inject TypeLiteral.
 *
 * <p>To create a type literal for {@code List<String>}, you can create an empty anonymous inner
 * class:
 *
 * <p>{@code TypeParameter<List<String>> list = new TypeParameter<List<String>>() {};}
 */
public abstract class TypeParameter<T> {
  protected final Type type;

  protected TypeParameter() {
    Type superClass = this.getClass().getGenericSuperclass();
    if (superClass instanceof Class) {
      throw new IllegalArgumentException("TypeParameter created without type information.");
    } else {
      this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }
  }

  public Type getType() {
    return this.type;
  }

  @Override
  public String toString() {
    return type.toString();
  }
}
