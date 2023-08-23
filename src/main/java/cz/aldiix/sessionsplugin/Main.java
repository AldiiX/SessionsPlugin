package cz.aldiix.sessionsplugin;

import cz.aldiix.sessionsplugin.commands.SessionCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        System.out.println("Sessions enabled!");
        Config.init();
        Controller.init();

        this.getCommand("session").setExecutor(new SessionCommand());
        this.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Controller.init();
    }

    /*@EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        if(plugin.getServer().getOnlinePlayers().size() <= 1) return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        if(message.startsWith("!")) {

        }
    }*/
}
