package io.freefair.android.injection.injector;

import android.app.Application;

public class ApplicationInjector extends AndroidResourceInjector<Application> {

    public ApplicationInjector(Application object, Object... possibleParents) {
        super(object, possibleParents);
    }
    
}
