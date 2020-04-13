/*
 *
 * Copyright 2020 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http:www.apache.orglicensesLICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.flipkart.gojira.core.aspect.test;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface AspectJConfig {

    /**
     * Use this to append additional classpath entries.
     * This is especially useful if you want your tests to use different
     * aop.xml files each.
     * If your aop.xml file is located for example in
     * {@code /home/joe/test/META-INF/aop.xml} then add {@code /home/joe/test}
     * as classpathAddition.
     * Depending on your project setup this might also work with relative
     * parts.
     */
    String[] classpathAdditions() default "";
}