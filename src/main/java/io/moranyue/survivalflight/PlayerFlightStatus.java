package io.moranyue.survivalflight;

public class PlayerFlightStatus {
    public double speed;
    public boolean is_enabled;
    
    public PlayerFlightStatus(double speed, boolean is_enabled) {
        this.speed = speed;
        this.is_enabled = is_enabled;
    }
}