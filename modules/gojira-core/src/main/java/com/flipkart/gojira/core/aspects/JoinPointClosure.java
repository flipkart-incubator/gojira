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

import java.lang.reflect.AccessibleObject;
import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;

public abstract class JoinPointClosure implements Joinpoint {

  protected JoinPoint jp;

  public JoinPointClosure(JoinPoint joinPoint) {
    this.jp = joinPoint;
  }

  @Override
  public Object proceed() throws Throwable {
    return execute();
  }

  // for subclasses, renamed from proceed to avoid confusion in
  // AspectJ around advice.
  public abstract Object execute() throws Throwable;

  @Override
  public Object getThis() {
    return jp.getThis();
  }

  /**
   * Must return either a Field, Method or Constructor representing the entity at the joinPoint.
   *
   * @see org.aopalliance.intercept.Joinpoint#getStaticPart()
   */
  public abstract AccessibleObject getStaticPart();
}
