package io.moranyue.survivalflight;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
// import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import java.util.UUID;

public class StatusRecoveryListener implements Listener {
    private final SurvivalFlight plugin;

    public StatusRecoveryListener(SurvivalFlight plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on_player_teleport(PlayerChangedWorldEvent ev) {
        Player player = ev.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerFlightStatus status = plugin.get_player_data(uuid);
        plugin.getLogger().info(String.format("Player who teleported `%s`'s flight status: is_enabled = %s, speed = %.2f",
            player.getName(), String.valueOf(status.is_enabled), status.speed));
        if (status.is_enabled) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.setAllowFlight(true);
                player.setFlySpeed((float) status.speed / 10);
            }, 5);
        }
    }
    @EventHandler()
    public void on_player_respawn(PlayerRespawnEvent ev) {
        Player player = ev.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerFlightStatus status = plugin.get_player_data(uuid);
        plugin.getLogger().info(String.format("Player who respawned `%s`'s flight status: is_enabled = %s, speed = %.2f",
            player.getName(), String.valueOf(status.is_enabled), status.speed));
        if (status.is_enabled) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.setAllowFlight(true);
                player.setFlySpeed((float) status.speed / 10);
            }, 5);
        }
    }
}