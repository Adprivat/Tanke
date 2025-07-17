package de.tankstelle.manager.util.events;

import java.util.*;

public class GameEventBus {
    private final Map<Class<? extends GameEvent>, List<EventHandler<? extends GameEvent>>> handlers = new HashMap<>();

    public <T extends GameEvent> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    public <T extends GameEvent> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        List<EventHandler<? extends GameEvent>> list = handlers.get(eventType);
        if (list != null) {
            list.remove(handler);
        }
    }

    public <T extends GameEvent> void publish(T event) {
        List<EventHandler<? extends GameEvent>> list = handlers.get(event.getClass());
        if (list != null) {
            for (EventHandler<? extends GameEvent> handler : list) {
                @SuppressWarnings("unchecked")
                EventHandler<T> typedHandler = (EventHandler<T>) handler;
                typedHandler.handle(event);
            }
        }
    }
} 