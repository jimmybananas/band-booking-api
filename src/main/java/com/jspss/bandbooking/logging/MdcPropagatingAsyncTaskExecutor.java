package com.jspss.bandbooking.logging;

import org.slf4j.MDC;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MdcPropagatingAsyncTaskExecutor implements AsyncTaskExecutor {

    private final AsyncTaskExecutor delegate;

    public MdcPropagatingAsyncTaskExecutor(AsyncTaskExecutor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable task) {
        delegate.execute(wrap(task));  // wrap -> Runnable
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        delegate.execute(wrap(task), startTimeout);  // wrap -> Runnable
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate.submit(wrap(task));  // wrap -> Runnable
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        // IMPORTANT: use the Callable<T> overload of wrap()
        Callable<T> wrapped = wrap(task);
        return delegate.submit(wrapped);  // wrap -> Callable<T>
    }

    private Runnable wrap(Runnable task) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return () -> {
            if (context != null) {
                MDC.setContextMap(context);
            }
            try {
                task.run();
            } finally {
                MDC.clear();
            }
        };
    }

    private <T> Callable<T> wrap(Callable<T> task) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return () -> {
            if (context != null) {
                MDC.setContextMap(context);
            }
            try {
                return task.call();
            } finally {
                MDC.clear();
            }
        };
    }
}
