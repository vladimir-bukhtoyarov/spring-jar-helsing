package com.github.springjarhelsing;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class SpringJarHelsingTest {

    ClassPathXmlApplicationContext applicationContext;
    TestContext testContext;

    @Before
    public void init() {
        applicationContext = new ClassPathXmlApplicationContext("test-main-context.xml");
        testContext = (TestContext) applicationContext.getBean("testContext");
    }

    @After
    public void shutdown() {
        applicationContext.close();
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        Callable<Integer> task = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 42;
            }
        };
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(task);

        int result09 = testContext.getGuava09UninterpretableFutureGetter().get(future);
        int result17 = testContext.getGuava17UninterpretableFutureGetter().get(future);
        assertEquals(result09, result17);
    }

}