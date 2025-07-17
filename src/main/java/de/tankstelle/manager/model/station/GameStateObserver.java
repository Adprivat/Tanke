package de.tankstelle.manager.model.station;

public interface GameStateObserver {
    void onGameStateChanged(GameState state);
} 