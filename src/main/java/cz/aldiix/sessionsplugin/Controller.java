package cz.aldiix.sessionsplugin;

import com.google.errorprone.annotations.Var;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

            List<Map<?, ?>> playersList = sessionData.getMapList("players");
            if(playersList.isEmpty()) continue;

            for (Map<?, ?> playerDataMap : playersList) {
                if (playerDataMap instanceof ConfigurationSection) {
                    ConfigurationSection playerData = (ConfigurationSection) playerDataMap;
                    String playerName = playerData.getString("name");
                    String playerRole = playerData.getString("role");
                    session.members.add(new Member(playerName, playerRole));
                }
            }

            sessions.add(session);
        }
    }

    public static void init() {
        new Controller().refresh();
    }
}
