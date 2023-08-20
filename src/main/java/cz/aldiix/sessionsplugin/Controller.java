package cz.aldiix.sessionsplugin;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

import static cz.aldiix.sessionsplugin.Config.config;

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



    public void refresh() {
        sessions.clear();

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
        }
    }

    public static void init() {
        new Controller().refresh();
    }
}
