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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * <p>Use this JUnit Runner if you want to enable AspectJ load time weaving in
 * your test. To use this runner place this annotation on your test class:</p>
 * <p>{@code @RunWith(AspectJUnit4Runner.class)}</p>
 *
 */
public class AspectJUnit4Runner extends BlockJUnit4ClassRunner {
    private WeavingURLClassLoader cl;
    private TestClass testClass;

    public AspectJUnit4Runner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    protected TestClass createTestClass(Class<?> clazz) {
        URL[] classpath = computeClasspath(clazz);
        cl = new WeavingURLClassLoader(classpath, null);
        clazz = loadClassFromClassLoader(clazz, cl);
        testClass = new TestClass(clazz);
        return testClass;
    }

    private URL[] computeClasspath(Class<?> clazz) {
        URLClassLoader originalClassLoader = (URLClassLoader)clazz.getClassLoader();
        URL[] classpath = originalClassLoader.getURLs();
        AspectJConfig config = clazz.getAnnotation(AspectJConfig.class);
        if(config != null) {
            classpath = appendToClasspath(classpath, config.classpathAdditions());
        }
        return classpath;
    }

    private URL[] appendToClasspath(URL[] classpath, String[] urls) {
        URL[] extended = Arrays.copyOf(classpath, classpath.length + urls.length);
        for(int i = 0; i < urls.length; i++) {
            URL url;
            try {
                url = Paths.get(urls[i]).toAbsolutePath().toUri().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            extended[classpath.length + i] = url;
        }
        return extended;
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        Class<? extends Annotation> test = loadClassFromClassLoader(Test.class, cl);
        return getTestClass().getAnnotatedMethods(test);
    }

    @Override
    public void run(final RunNotifier notifier) {
        Throwable firstException = null;
        try {
            super.run(notifier);
        } catch (Exception e) {
            firstException = e;
            throw e;
        } finally {
            try {
                cl.close();
            } catch (IOException e) {
                RuntimeException rte = new RuntimeException("Failed to close AspectJ classloader.", e);
                if(firstException != null) {
                    rte.addSuppressed(firstException);
                }
                throw rte;
            }
        }
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        Class<? extends Annotation> beforeClass = loadClassFromClassLoader(BeforeClass.class, cl);
        List<FrameworkMethod> befores = testClass.getAnnotatedMethods(beforeClass);
        return befores.isEmpty() ? statement : new RunBefores(statement, befores, null);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        Class<? extends Annotation> afterClass = loadClassFromClassLoader(AfterClass.class, cl);
        List<FrameworkMethod> afters = testClass.getAnnotatedMethods(afterClass);
        return afters.isEmpty() ? statement : new RunAfters(statement, afters, null);
    }

    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        Class<? extends Annotation> before = loadClassFromClassLoader(Before.class, cl);
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(before);
        return befores.isEmpty() ? statement : new RunBefores(statement, befores, target);
    }

    @Override
    protected Statement withAfters(FrameworkMethod method, Object target, Statement statement) {
        Class<? extends Annotation> after = loadClassFromClassLoader(After.class, cl);
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(after);
        return afters.isEmpty() ? statement : new RunAfters(statement, afters, target);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> loadClassFromClassLoader(Class<T> clazz, ClassLoader cl) {
        Class<T> loaded;
        try {
            loaded = (Class<T>) Class.forName(clazz.getName(), true, cl);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return loaded;
    }

}