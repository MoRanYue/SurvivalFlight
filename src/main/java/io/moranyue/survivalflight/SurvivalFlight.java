package io.moranyue.survivalflight;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SurvivalFlight extends JavaPlugin {
    private Logger logger;
    private Map<UUID, PlayerFlightStatus> player_flight_status = new HashMap<>();
    private File player_data_file;
    private FileConfiguration player_data_config;

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("The plugin is loading.");

        saveDefaultConfig();
        reloadConfig();
        setup_player_data_storage();

        FileConfiguration config = getConfig();

        boolean is_config_changed = false;
        double max_speed = config.getDouble("fly_command.max_speed", 5.0);
        double default_speed = config.getDouble("fly_command.default_speed");
        if (max_speed < default_speed) {
            logger.warning("fly_command.max_speed is less than fly_command.default_speed, setting the latter to the former.");
            config.set("fly_command.default_speed", max_speed);
            default_speed = max_speed;
            is_config_changed = true;
        }
        if (max_speed < 0.1) {
            logger.warning("fly_command.max_speed is less than the minimum value 0.1, setting to default value 5.0.");
            config.set("fly_command.max_speed", 5.0);
            is_config_changed = true;
        }
        if (default_speed < 0.1) {
            logger.warning("fly_command.default_speed is less than the minimum value 0.1, setting to default value 1.0.");
            config.set("fly_command.default_speed", 1.0);
            is_config_changed = true;
        }

        if (is_config_changed) {
            saveConfig();
        }

        PluginCommand fly_command = getCommand("fly");
        if (fly_command == null) {
            logger.severe("Failed to register /fly command.");
            return;
        }

        fly_command.setExecutor(new FlyCommand(this));
        
        PluginManager plugin_manager = getServer().getPluginManager();
        plugin_manager.registerEvents(new StatusRecoveryListener(this), this);
        plugin_manager.registerEvents(new PlayerDataStorageListener(this), this);

        logger.info("The plugin has loaded.");
    }

    @Override
    public void onDisable() {
        save_players_data();
        logger.info("Players' data has been saved.");
        logger.info("The plugin has been disabled.");
    }
    
    private void setup_player_data_storage() {
        player_data_file = new File(getDataFolder(), "player_flight_speed.yml");
        if (!player_data_file.exists()) {
            saveResource("player_flight_speed.yml", false);
        }
        player_data_config = YamlConfiguration.loadConfiguration(player_data_file);
        load_players_data();
    }
    private void load_players_data() {
        if (player_data_config.contains("flight_speed")) {
            for (String uuid_str : player_data_config.getConfigurationSection("flight_speed").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuid_str);
                    
                    player_flight_status.put(uuid, new PlayerFlightStatus(player_data_config.getDouble("flight_speed." + uuid_str + ".speed", 1.0), player_data_config.getBoolean("flight_speed." + uuid_str + ".is_enabled", false)));
                }
                catch (IllegalArgumentException err) {
                    logger.warning("Invalid UUID: " + uuid_str + ".");
                }
            }
        }
    }
    
    private void save_players_data() {
        for (Map.Entry<UUID, PlayerFlightStatus> entry : player_flight_status.entrySet()) {
            String key = entry.getKey().toString();
            PlayerFlightStatus value = entry.getValue();
            player_data_config.set("flight_speed." + key + ".is_enabled", value.is_enabled);
            player_data_config.set("flight_speed." + key + ".speed", value.speed);
        }
        
        try {
            player_data_config.save(player_data_file);
        }
        catch (IOException err) {
            logger.severe("Failed to save player data: " + err);
        }
    }
    
    public void set_player_data(UUID uuid, double speed, boolean is_enabled) {
        player_flight_status.put(uuid, new PlayerFlightStatus(speed, is_enabled));
        save_player_data(uuid);
    }
    
    public PlayerFlightStatus get_player_data(UUID uuid) {
        return player_flight_status.getOrDefault(uuid, new PlayerFlightStatus(getConfig().getDouble("fly_command.default_speed", 1.0), false));
    }

    public void save_player_data(UUID uuid) {
        if (player_flight_status.containsKey(uuid)) {
            PlayerFlightStatus status = player_flight_status.get(uuid);
            player_data_config.set("flight_speed." + uuid.toString() + ".is_enabled", status.is_enabled);
            player_data_config.set("flight_speed." + uuid.toString() + ".speed", status.speed);
            try {
                player_data_config.save(player_data_file);
            } catch (IOException err) {
                logger.severe("Failed to save player data: " + err);
            }
        }
    }
}
