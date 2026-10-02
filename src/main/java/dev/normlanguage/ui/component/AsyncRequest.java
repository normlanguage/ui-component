package dev.normlanguage.ui.component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

final class AsyncRequest<T> {
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private final AtomicReference<CompletableFuture<T>> source = new AtomicReference<>();
    private final CompletableFuture<T> result;

    AsyncRequest(Supplier<CompletableFuture<T>> provider) {
        result = CompletableFuture.supplyAsync(() -> {
            var future = Objects.requireNonNull(provider.get());
            source.set(future);
            if (cancelled.get()) future.cancel(true);
            return future;
        }).thenCompose(Function.identity());
    }

    CompletableFuture<T> result() { return result; }

    void cancel() {
        cancelled.set(true);
        var future = source.get();
        if (future != null) future.cancel(true);
        result.cancel(true);
    }
}
