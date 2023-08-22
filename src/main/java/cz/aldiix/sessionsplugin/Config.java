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

        if(config.get("prefix") == null) config.set("prefix", "§b§lSESSIONS§r§8 » §r");
        if(config.get("sessions") == null) config.set("sessions", "{}");
        if(config.get("messages.sessionSuccessfullyCreated") == null) config.set("messages.sessionSuccessfullyCreated", "The session has been successfully created.");
        if(config.get("messages.playerIsAlreadyConnectedToSessionError") == null) config.set("messages.playerIsAlreadyConnectedToSessionError", "You can't create a session because you're currently in one.");
        if(config.get("messages.playerIsNotOwnerSessionDeleteError") == null) config.set("messages.playerIsNotOwnerSessionDeleteError", "You can't delete this session, because you're not the owner of the session.");
        if(config.get("messages.sessionSuccessfullyDeleted") == null) config.set("messages.sessionSuccessfullyDeleted", "The session has been successfully deleted.");

        Variables.pluginPrefix = config.getString("prefix");
        Variables.Messages.sessionSuccessfullyCreated = config.getString("messages.sessionSuccessfullyCreated");
        Variables.Messages.playerIsAlreadyConnectedToSessionError = config.getString("messages.playerIsAlreadyConnectedToSessionError");
        Variables.Messages.playerIsNotOwnerSessionDeleteError = config.getString("messages.playerIsNotOwnerSessionDeleteError");
        Variables.Messages.sessionSuccessfullyDeleted = config.getString("messages.sessionSuccessfullyDeleted");
        save();
    }
}
