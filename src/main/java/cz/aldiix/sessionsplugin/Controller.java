package cz.aldiix.sessionsplugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cz.aldiix.sessionsplugin.Config.config;
import static cz.aldiix.sessionsplugin.SessionsPlugin.plugin;

public class Controller {

    private class Member {
        public Member(String name, String role) {
            this.name = name;
            this.role = role;
        }
        public String name;
        public String role;
    }

    public class Session {
        public Session() {
            ids++;
            id = ids;
        }

        private static int ids;
        public int id;
        public String name;
        public List<Member> members = new ArrayList<>();
    }

    public static List<Session> sessions = new ArrayList<>();



    public static int getPlayersSessionID(Player player) {
        ConfigurationSection sessionsSection = config.getConfigurationSection("sessions");

        for (String key : sessionsSection.getKeys(false)) {
            ConfigurationSection sessionData = sessionsSection.getConfigurationSection(key);
            if (sessionData == null) continue;

            ConfigurationSection playersList = sessionData.getConfigurationSection("players");
            if(playersList == null) continue;

            for (String key2 : playersList.getKeys(false)) {
                ConfigurationSection playerData = (ConfigurationSection) playersList.get(key2);
                if(Objects.equals(playerData.getString("name"), player.getDisplayName())) return Integer.parseInt(key);
            }
        }

        return -1;
    }



    public void refresh() {
        /*sessions.clear();

        ConfigurationSection sessionsSection = config.getConfigurationSection("sessions");
        if (sessionsSection == null) return;

        for (String key : sessionsSection.getKeys(false)) {
            ConfigurationSection sessionData = sessionsSection.getConfigurationSection(key);
            if (sessionData == null) continue;

            Session session = new Session();
            session.id = Integer.parseInt(key);
            session.name = sessionData.getString("name");

            ConfigurationSection playersList = sessionData.getConfigurationSection("players");
            if(playersList == null) continue;

            for (String k2 : playersList.getKeys(false)) {
                ConfigurationSection playerData = (ConfigurationSection) playersList.get(k2);
                String playerName = playerData.getString("name");
                String playerRole = playerData.getString("role");
                session.members.add(new Member(playerName, playerRole));
            }

            sessions.add(session);
        }*/



        for (Player player : plugin.getServer().getOnlinePlayers()) {

            // show all players to everyone
            for(Player p : plugin.getServer().getOnlinePlayers()) {
                player.showPlayer(plugin, p);
            }

            //if(plugin.getServer().getOnlinePlayers().size() <= 1) break;



            // hide all players
            int playerSessionId = getPlayersSessionID(player);

            for(Player p : plugin.getServer().getOnlinePlayers()) {
                player.hidePlayer(plugin, p);
            }



            // if player is not in any session, then show every player who is not in any session
            if(playerSessionId == -1) {
                for(Player p : plugin.getServer().getOnlinePlayers()) {
                    if(getPlayersSessionID(p) == -1) {
                        player.showPlayer(plugin, p);
                    }
                }

                continue;
            }


            // show session players
            ConfigurationSection players = config.getConfigurationSection("sessions." + playerSessionId + ".players");
            if(players == null) continue;

            for (String key : players.getKeys(false)) {
                Player p = plugin.getServer().getPlayer(players.getString(key + ".name"));
                if(p == null) continue;

                player.showPlayer(plugin, p);
            }
        }
    }

    public static void init() {
        new Controller().refresh();
    }
}
