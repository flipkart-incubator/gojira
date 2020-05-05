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

package com.flipkart.gojira.serde.annotations;

import com.flipkart.gojira.serde.handlers.TestSerdeHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SerdeHandler annotation on method parameters can be used to provide custom serialization handlers
 * that override default serialization handlers. This is especially useful when serialization of
 * objects are method specific and not class specific.
 *
 * <p>Using ElementType.TYPE_USE as a target annotation for SerdeHandler does not work with Guice
 * for now. For the time being, using ElementType.Method for custom return type serdehandler.
 *
 * <p>Refer: https://github.com/google/guice/issues/1193#issuecomment-402756883 (nearest possible
 * document w.r.t. the mentioned issue)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface SerdeHandler {

  /**
   * If this annotation is added, during runtime, {@link
   * com.flipkart.gojira.serde.SerdeHandlerRepository} will create an instance of the .class file
   * specified with the annotation. Provide a class with a public no-args constructor.
   *
   * @return .class whose instance needs to be used for serialization/deserialization.
   */
  Class<? extends TestSerdeHandler> serdeHandlerClass();
}
