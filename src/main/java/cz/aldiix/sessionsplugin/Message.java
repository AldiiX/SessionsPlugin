package cz.aldiix.sessionsplugin;

import org.bukkit.entity.Player;

import static cz.aldiix.sessionsplugin.Variables.pluginPrefix;

public final class Message {

    public enum Type {
        NORMAL, ERROR, SUCCESS, ANNOUNCE, WARN
    }

    public static void send(Type type, Player player, String message) {
        switch (type) {
            case NORMAL, ANNOUNCE, WARN -> player.sendMessage(pluginPrefix + "§e" + message);
            case ERROR -> player.sendMessage(pluginPrefix + "§c" + message);
            case SUCCESS -> player.sendMessage(pluginPrefix + "§a" + message);
        }
    }
}
