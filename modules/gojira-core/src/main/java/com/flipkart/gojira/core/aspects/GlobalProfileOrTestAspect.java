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

package com.flipkart.gojira.core.aspects;

import com.flipkart.gojira.core.ProfileOrTestMethodInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.Aspect;

/**
 * Use this class in aop.xml to extend to while defining concrete-aspects for defining custom
 * pointcuts.
 */
@Aspect
public abstract class GlobalProfileOrTestAspect extends AopAllianceAdapter {

  @Override
  protected MethodInterceptor getMethodInterceptor() {
    return new ProfileOrTestMethodInterceptor();
  }
}
