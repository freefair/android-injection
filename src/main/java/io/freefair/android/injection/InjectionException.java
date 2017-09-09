package io.freefair.android.injection;

/**
 * @author Lars Grefer
 */
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
