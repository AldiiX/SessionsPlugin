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

import static cz.aldiix.sessionsplugin.Config.config;

public class SessionCommand implements CommandExecutor, TabCompleter {

    private String[] args;


    // Util methods
    private boolean checkIfPlayerHasSession(Player player, ConfigurationSection sessionsSection) {
        for (String key : sessionsSection.getKeys(false)) {
            ConfigurationSection sessionData = sessionsSection.getConfigurationSection(key);
            if (sessionData == null) continue;

            ConfigurationSection playersList = sessionData.getConfigurationSection("players");
            if(playersList == null) continue;

            for (String key2 : playersList.getKeys(false)) {
                ConfigurationSection playerData = (ConfigurationSection) playersList.get(key2);
                if(Objects.equals(playerData.getString("name"), player.getDisplayName())) return true;
            }
        }

        return false;
    }

    private int getPlayersSessionID(Player player) {
        return -1;
    }



    // Main methods
    private void createSession(Player player) {
        String sessionName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String playerName = player.getDisplayName();

        // sessions query
        ConfigurationSection sessionsSection = config.getConfigurationSection("sessions");
        int nextSessionIndex = sessionsSection != null ? sessionsSection.getKeys(false).size() : 0;

        // check if player is already in a session
        if(nextSessionIndex != 0 && checkIfPlayerHasSession(player, sessionsSection)) {
            Message.send(Message.Type.ERROR, player, Variables.Messages.playerIsAlreadyConnectedToSessionError);
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
        Message.send(Message.Type.SUCCESS, player, Variables.Messages.sessionSuccessfullyCreated);
    }

    private void deleteSession(Player player) {
        int id = getPlayersSessionID(player);

        if(id < 0) return;
    }


    // CmdExecutor methods
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof ConsoleCommandSender) return false;

        Player player = (Player) sender;
        this.args = args;


        if(args.length > 0) switch (args[0]) {
            case "create" -> createSession(player);
            case "invite" -> {

            }
            case "delete" -> deleteSession(player);
            /*default -> {
                Message.send(Message.Type.ERROR, player, "");
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