package io.moranyue.survivalflight;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

public class FlyCommand implements CommandExecutor {
    private final SurvivalFlight plugin;
    public FlyCommand(SurvivalFlight plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + config.getString("messages.players_only_command", "Only players can use this command."));
            return true;
        }

        Player player = (Player) sender;
        GameMode game_mode = player.getGameMode();
        if (game_mode != GameMode.SURVIVAL && game_mode != GameMode.ADVENTURE) {
            player.sendMessage(ChatColor.RED + config.getString("messages.survival_or_adventure_only_command", "Only player who are playing Survival or Adventure can use this command."));
            return true;
        }
        
        double max_speed = config.getDouble("fly_command.max_speed", 5.0);

        if (args.length == 0) {
            toggle_flight(player, plugin.get_player_data(player.getUniqueId()).speed, config);
            return true;
        }
        else if (args.length == 1) {
            try {
                double speed = Double.parseDouble(args[0]);
                set_flight_speed(player, speed, max_speed, config);
            }
            catch (NumberFormatException err) {
                player.sendMessage(ChatColor.RED + config.getString("messages.speed_must_be_a_number", "The speed must be a number."));
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + config.getString("messages.fly_command_usage", "Usage: /fly [speed]"));

        return true;
    }

    private void toggle_flight(Player player, double speed, FileConfiguration config) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            plugin.get_player_data(player.getUniqueId()).is_enabled = false;
            player.sendMessage(ChatColor.RED + config.getString("messages.flight_is_disabled", "The flight is disabled."));
        }
        else {
            player.setAllowFlight(true);
            player.setFlySpeed((float) (speed / 10));
            plugin.set_player_data(player.getUniqueId(), speed, true);
            player.sendMessage(ChatColor.GREEN + config.getString("messages.flight_is_enabled", "The flight is enabled."));
            player.sendMessage(String.format(
                ChatColor.GREEN + config.getString("messages.set_flight_speed", "Setting the flight speed to %.2f."),
                speed
            ));
        }
    }

    private void set_flight_speed(Player player, double speed, double max_speed, FileConfiguration config) {
        if (speed < 0.1 || speed > max_speed) {
            player.sendMessage(String.format(
                ChatColor.RED + config.getString("messages.speed_must_be_in_range", "The speed must be between 0.1 and %.2f."),
                max_speed
            ));
            return;
        }

        if (!player.getAllowFlight()) {
            player.setAllowFlight(true);
            player.sendMessage(ChatColor.GREEN + config.getString("messages.flight_is_enabled", "The flight is enabled"));
        }

        player.setFlySpeed((float) (speed / 10));
        plugin.set_player_data(player.getUniqueId(), speed, true);
        player.sendMessage(String.format(
            ChatColor.GREEN + config.getString("messages.set_flight_speed", "Setting the flight speed to %.2f."),
            speed
        ));
    }
}