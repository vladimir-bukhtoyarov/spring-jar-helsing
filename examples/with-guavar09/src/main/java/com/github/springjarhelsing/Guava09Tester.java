package com.github.springjarhelsing;

import com.google.common.util.concurrent.Futures;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.*;

public class Guava09Tester {

    public Properties getProperties() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Properties> future = executor.submit(new Callable<Properties>() {
            @Override
            public Properties call() throws Exception {
                return System.getProperties();
            }
        });

        // this code can not be compiled with guava 17
        future = Futures.makeUninterruptible(future);

        Properties result = future.get();
        executor.shutdown();
        return result;
    }

    public String getKey() throws IOException {
        Properties properties = new Properties();
        properties.load(Guava09Tester.class.getResourceAsStream("example.properties"));
        return properties.getProperty("key");
    }

}
