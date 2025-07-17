package de.tankstelle.manager.util.events;

public interface EventHandler<T extends GameEvent> {
    void handle(T event);
} 