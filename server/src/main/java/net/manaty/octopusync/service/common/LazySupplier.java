package net.manaty.octopusync.service.common;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class LazySupplier<T> implements Supplier<T> {

    public static <T> LazySupplier<T> lazySupplier(Supplier<T> delegate) {
        return new LazySupplier<>(delegate);
    }

    private final Supplier<T> delegate;
    private final Object lock;

    private volatile T value;

    private LazySupplier(Supplier<T> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.lock = new Object();
    }

    @Override
    public @Nonnull T get() {
        if (value == null) {
            synchronized (lock) {
                if (value == null) {
                    value = Objects.requireNonNull(delegate.get());
                }
            }
        }
        return value;
    }
}
