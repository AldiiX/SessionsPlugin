package cz.aldiix.sessionsplugin;

import cz.aldiix.sessionsplugin.commands.SessionCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static cz.aldiix.sessionsplugin.Config.config;

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

    @EventHandler
    public void onPlayerMessage(PlayerChatEvent event) {
        if(plugin.getServer().getOnlinePlayers().size() <= 1) return;

        Player player = event.getPlayer();
        int playerSessionId = Controller.getPlayersSessionID(player);
        String message = event.getMessage();



        if (message.startsWith("!")) {
            event.setMessage(message.substring(1));
            event.setFormat("§8[ALL]§r <%1$s> %2$s");
            return;
        }

        if(playerSessionId == -1) {
            for(Player p : plugin.getServer().getOnlinePlayers()) {
                if(Controller.getPlayersSessionID(p) == -1) {
                    p.sendMessage(String.format("<%s> %s", player.getDisplayName(), message));
                }
            }

            System.out.println(String.format("<%s> %s", player.getDisplayName(), message));
            event.setCancelled(true);
            return;
        }



        ConfigurationSection players = config.getConfigurationSection("sessions." + playerSessionId + ".players");

        for(String key : players.getKeys(false)) {
            Player p = plugin.getServer().getPlayer(players.getString(key + ".name"));
            p.sendMessage(String.format("§8[S]§r <%s> %s", player.getDisplayName(), message));
        }

        System.out.println(String.format("[S%d] <%s> %s", playerSessionId, player.getDisplayName(), message));
        event.setCancelled(true);
    }
}