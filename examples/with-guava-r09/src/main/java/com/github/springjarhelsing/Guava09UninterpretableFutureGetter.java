package com.github.springjarhelsing;

import com.google.common.util.concurrent.Futures;
import java.util.concurrent.*;

public class Guava09UninterpretableFutureGetter implements UninterpretableFutureGetter {

    @Override
    public <T> T get(Future<T> future) throws ExecutionException, InterruptedException {
        // this code can not be compiled with guava 17
        return Futures.makeUninterruptible(future).get();
    }

}
