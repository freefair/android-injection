package io.freefair.android.injection;

public class InjectionException extends RuntimeException {
    public InjectionException() {
    }

    public InjectionException(String detailMessage) {
        super(detailMessage);
    }

    public InjectionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InjectionException(Throwable throwable) {
        super(throwable);
    }
}
