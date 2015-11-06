package com.github.springjarhelsing;

import java.util.concurrent.*;

public interface UninterpretableFutureGetter {

    <T> T get(Future<T> future) throws ExecutionException, InterruptedException;

}
