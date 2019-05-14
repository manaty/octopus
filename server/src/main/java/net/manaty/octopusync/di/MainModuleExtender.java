package net.manaty.octopusync.di;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ModuleExtender;
import net.manaty.octopusync.service.EventListener;

public class MainModuleExtender extends ModuleExtender<MainModuleExtender> {

    private Multibinder<EventListener> eventListeners;

    public MainModuleExtender(Binder binder) {
        super(binder);
    }

    @Override
    public MainModuleExtender initAllExtensions() {
        contributeEventListeners();

        return this;
    }

    public MainModuleExtender addEventListener(EventListener listener) {
        contributeEventListeners().addBinding().toInstance(listener);
        return this;
    }

    public MainModuleExtender addEventListenerType(Class<? extends EventListener> listenerType) {
        contributeEventListeners().addBinding().to(listenerType);
        return this;
    }

    private Multibinder<EventListener> contributeEventListeners() {
        return eventListeners != null ? eventListeners : (eventListeners = newSet(EventListener.class));
    }
}
