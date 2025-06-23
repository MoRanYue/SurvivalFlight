package io.moranyue.survivalflight;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.logging.Logger;
import io.moranyue.survivalflight.FlyCommand;

public class SurvivalFlight extends JavaPlugin {
    private Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("The plugin is loading.");

        saveDefaultConfig();
        reloadConfig();

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
            logger.severe("Failed to register /fly command!");
            return;
        }

        fly_command.setExecutor(new FlyCommand());

        logger.info("The plugin has loaded.");
    }

    @Override
    public void onDisable() {
        logger.info("The plugin has been disabled.");
    }
}
