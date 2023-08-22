package cz.aldiix.sessionsplugin.commands;

import cz.aldiix.sessionsplugin.Config;
import cz.aldiix.sessionsplugin.Message;
import cz.aldiix.sessionsplugin.Variables;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static cz.aldiix.sessionsplugin.Config.*;
import static cz.aldiix.sessionsplugin.Message.Type.*;

public class SessionCommand implements CommandExecutor, TabCompleter {

    private String[] args;
    private Player player;


    // Util methods
    private boolean checkIfPlayerHasSession(ConfigurationSection sessionsSection) {
        return getPlayersSessionID() >= 0;
    }

    private int getPlayersSessionID() {
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

    private int getNextSessionIndex() {
        ConfigurationSection sessionsSection = config.getConfigurationSection("sessions");
        int size = sessionsSection.getKeys(false).size();

        for (int i = 0; i < size; i++) {
            String n = String.valueOf(i);

            if(sessionsSection.getString(n) == null) return Integer.parseInt(n);
        }

        return size;
    }



    // Main methods
    private void createSession() {
        String sessionName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String playerName = player.getDisplayName();

        // sessions query
        ConfigurationSection sessionsSection = config.getConfigurationSection("sessions");
        int nextSessionIndex = getNextSessionIndex();

        // check if player is already in a session
        if(nextSessionIndex != 0 && checkIfPlayerHasSession(sessionsSection)) {
            Message.send(ERROR, player, Variables.Messages.playerIsAlreadyConnectedToSessionError);
            return;
        }

        // add new session
        ConfigurationSection newSession = sessionsSection.createSection(String.valueOf(nextSessionIndex));
        newSession.set("sessionName", sessionName);
        newSession.set("owner", playerName);

        // add players section
        ConfigurationSection playersSection = newSession.createSection("players");

        ConfigurationSection newPlayer1 = playersSection.createSection("0");
        newPlayer1.set("name", playerName);
        newPlayer1.set("role", "Owner");

        Config.save();
        Message.send(SUCCESS, player, Variables.Messages.sessionSuccessfullyCreated);
    }

    private void deleteSession() {
        int id = getPlayersSessionID();

        if(id < 0) return;

        ConfigurationSection session = config.getConfigurationSection("sessions." + id);
        if(!Objects.equals(session.getString("owner"), player.getDisplayName())) {
            Message.send(ERROR, player, Variables.Messages.playerIsNotOwnerSessionDeleteError);
            return;
        }

        config.set("sessions." + id, null);
        Message.send(SUCCESS, player, Variables.Messages.sessionSuccessfullyDeleted);
        Config.save();
    }

    private void leaveSession() {
        
    }





    // CmdExecutor methods
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof ConsoleCommandSender) return false;

        Player player = (Player) sender;
        this.player = player;
        this.args = args;


        if(args.length > 0) switch (args[0]) {
            case "create" -> createSession();
            case "invite" -> {

            }
            case "delete" -> deleteSession();
            case "leave" -> leaveSession();
            /*default -> {
                Message.send(ERROR, player, "");
            }*/
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("create");
            completions.add("invite");
            completions.add("delete");
        } else if(args.length == 2 && Objects.equals(args[0], "create")) {
            completions.add("name");
        }

        return completions;
    }
}