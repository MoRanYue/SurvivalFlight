package io.moranyue.survivalflight;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.EventPriority;
import java.util.UUID;

public class StatusRecoveryListener implements Listener {
    private final SurvivalFlight plugin;

    public StatusRecoveryListener(SurvivalFlight plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on_player_teleport(PlayerTeleportEvent ev) {
        Player player = ev.getPlayer();
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            PlayerFlightStatus status = plugin.get_player_data(uuid);
            plugin.getLogger()
                    .info(String.format("Teleported player `%s`'s flight status: is_enabled = %s, speed = %.2f",
                        player.getName(), String.valueOf(status.is_enabled), status.speed));
            if (status.is_enabled) {
                Player new_player = Bukkit.getPlayer(player.getUniqueId());
                new_player.setAllowFlight(true);
                new_player.setFlySpeed((float) status.speed / 10);
            }
        }, 1);
    }
    @EventHandler()
    public void on_player_respawn(PlayerRespawnEvent ev) {
        Player player = ev.getPlayer();
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            PlayerFlightStatus status = plugin.get_player_data(uuid);
            plugin.getLogger()
                    .info(String.format("Respawned player `%s`'s flight status: is_enabled = %s, speed = %.2f",
                        player.getName(), String.valueOf(status.is_enabled), status.speed));
            if (status.is_enabled) {
                Player new_player = Bukkit.getPlayer(player.getUniqueId());
                new_player.setAllowFlight(true);
                new_player.setFlySpeed((float) status.speed / 10);
            }
        }, 1);
    }
}