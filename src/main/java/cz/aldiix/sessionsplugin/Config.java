package cz.aldiix.sessionsplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static cz.aldiix.sessionsplugin.Main.plugin;

public class Config {

    private static final File file = new File(plugin.getDataFolder(), "config.yml");
    public static FileConfiguration config;

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("ERR");
        }
    }

    public static void init() {
        config = YamlConfiguration.loadConfiguration(file);

        if(config.get("prefix") == null) config.set("prefix", "§a§lSESSIONS§r§8 >> §r");
        if(config.get("messages.sessionSuccessfullyCreated") == null) config.set("messages.sessionSuccessfullyCreated", "Session successfully created.");

        Variables.pluginPrefix = config.getString("prefix");
        Variables.Messages.sessionSuccessfullyCreated = config.getString("messages.sessionSuccessfullyCreated");
        save();
    }
}
