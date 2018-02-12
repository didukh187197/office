package com.company.office.broadcast;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@Component
public class LogsCreatedEventBroadcaster {
    public final List<WeakReference<Consumer<LogsCreatedEvent>>> subscriptions = new ArrayList<>();

    @EventListener
    protected void onMessage(LogsCreatedEvent event) {
        synchronized (subscriptions) {
            Iterator<WeakReference<Consumer<LogsCreatedEvent>>> iterator = subscriptions.iterator();
            while (iterator.hasNext()) {
                WeakReference<Consumer<LogsCreatedEvent>> reference = iterator.next();
                Consumer<LogsCreatedEvent> eventConsumer = reference.get();
                if (eventConsumer == null) {
                    iterator.remove();
                } else {
                    eventConsumer.accept(event);
                }
            }
        }
    }

    public void subscribe(Consumer<LogsCreatedEvent> handler) {
        synchronized (subscriptions) {
            subscriptions.add(new WeakReference<>(handler));
        }
    }

}
