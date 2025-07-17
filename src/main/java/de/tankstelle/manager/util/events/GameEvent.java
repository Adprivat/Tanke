package de.tankstelle.manager.util.events;

public abstract class GameEvent {
    private final long timestamp;

    public GameEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
} 