package com.company.office.broadcast;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@Component
public class LogCreateEventBroadcaster {
    public final List<WeakReference<Consumer<LogCreateEvent>>> subscriptions = new ArrayList<>();

    @EventListener
    protected void onMessage(LogCreateEvent event) {
        synchronized (subscriptions) {
            Iterator<WeakReference<Consumer<LogCreateEvent>>> iterator = subscriptions.iterator();
            while (iterator.hasNext()) {
                WeakReference<Consumer<LogCreateEvent>> reference = iterator.next();
                Consumer<LogCreateEvent> eventConsumer = reference.get();
                if (eventConsumer == null) {
                    iterator.remove();
                } else {
                    eventConsumer.accept(event);
                }
            }
        }
    }

    public void subscribe(Consumer<LogCreateEvent> handler) {
        synchronized (subscriptions) {
            subscriptions.add(new WeakReference<>(handler));
        }
    }

}
