package io.freefair.android.injection;

import lombok.NoArgsConstructor;

/**
 * @author Lars Grefer
 */
@NoArgsConstructor
public class InjectionException extends RuntimeException {

    public InjectionException(String detailMessage) {
        super(detailMessage);
    }

    public InjectionException(Throwable throwable) {
        super(throwable);
    }
}
