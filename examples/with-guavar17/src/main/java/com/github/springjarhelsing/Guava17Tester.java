package com.github.springjarhelsing;

import com.google.common.util.concurrent.Uninterruptibles;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.*;

public class Guava17Tester {

    public Properties getProperties() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Properties> future = executor.submit(new Callable<Properties>() {
            @Override
            public Properties call() throws Exception {
                return System.getProperties();
            }
        });

        // this code can not be compiled with guava r09
        Properties result = Uninterruptibles.getUninterruptibly(future);
        executor.shutdown();
        return result;
    }

}
