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

package com.flipkart.gojira.compare.annotations;

import com.flipkart.compare.handlers.TestCompareHandler;
import com.flipkart.gojira.compare.GojiraCompareHandlerRepository;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CompareHandler annotation on method parameters can be used to provide custom compare handlers
 * that override default compare handlers. This is especially useful when comparison of objects are
 * method specific and not class specific.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CompareHandler {

  /**
   * If this annotation is added, during runtime, {@link GojiraCompareHandlerRepository} will create
   * an instance of the .class file specified with the annotation. Provide a class with a public
   * no-args constructor.
   *
   * @return .class whose instance needs to be used for comparison
   */
  Class<? extends TestCompareHandler> compareHandlerClass();
}
