package com.github.springjarhelsing;

import com.google.common.util.concurrent.Uninterruptibles;
import java.util.concurrent.*;

public class Guava17UninterpretableFutureGetter implements UninterpretableFutureGetter {

    @Override
    public <T> T get(Future<T> future) throws ExecutionException, InterruptedException {
        return Uninterruptibles.getUninterruptibly(future);
    }

}
