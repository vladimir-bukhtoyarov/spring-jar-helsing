package com.github.springjarhelsing;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutionException;

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
        testContext.getGuava09Tester().getProperties();
        testContext.getGuava17Tester().getProperties();
    }

}