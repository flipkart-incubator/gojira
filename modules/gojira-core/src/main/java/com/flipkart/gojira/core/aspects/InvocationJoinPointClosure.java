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
import org.aopalliance.intercept.Invocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

public abstract class InvocationJoinPointClosure extends JoinPointClosure implements Invocation {

  public InvocationJoinPointClosure(JoinPoint jp) {
    super(jp);
  }

  @Override
  public AccessibleObject getStaticPart() {
    CodeSignature codeSignature = (CodeSignature) jp.getSignature();
    Class clazz = codeSignature.getDeclaringType();
    AccessibleObject ret = null;
    try {
      if (codeSignature instanceof MethodSignature) {
        ret = clazz.getMethod(codeSignature.getName(), codeSignature.getParameterTypes());
      }
    } catch (NoSuchMethodException e) {
      try {
        ret = clazz.getDeclaredMethod(codeSignature.getName(), codeSignature.getParameterTypes());
      } catch (NoSuchMethodException e1) {
        throw new UnsupportedOperationException(
            "Can't find member " + codeSignature.toLongString());
      }
    }
    return ret;
  }

  @Override
  public Object[] getArguments() {
    return jp.getArgs();
  }
}
