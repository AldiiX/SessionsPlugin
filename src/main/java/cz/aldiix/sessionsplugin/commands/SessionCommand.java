package cz.aldiix.sessionsplugin.commands;

import cz.aldiix.sessionsplugin.Config;
import cz.aldiix.sessionsplugin.Message;
import cz.aldiix.sessionsplugin.Variables;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static cz.aldiix.sessionsplugin.Config.config;
import static cz.aldiix.sessionsplugin.Main.plugin;

public class SessionCommand implements CommandExecutor {

    private String[] args;

    private void createSession(Player player) {
        String sessionName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String playerName = player.getDisplayName();

        // sessions query
        ConfigurationSection sessionsSection = config.getConfigurationSection("sessions");
        int nextSessionIndex = sessionsSection.getKeys(false).size();

        // add new session
        ConfigurationSection newSession = sessionsSection.createSection(String.valueOf(nextSessionIndex));
        newSession.set("sessionName", sessionName);
        newSession.set("owner", playerName);

        // add players section
        ConfigurationSection playersSection = newSession.createSection("players");

        ConfigurationSection newPlayer1 = playersSection.createSection("0");
        newPlayer1.set("sessionName", playerName);
        newPlayer1.set("role", "Owner");

        Config.save();
        Message.send(Message.Type.SUCCESS, player, Variables.Messages.sessionSuccessfullyCreated);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof ConsoleCommandSender) return false;

        Player player = (Player) sender;
        this.args = args;


        switch(args[0]) {
            case "create": createSession(player); break;

            case "invite": ; break;
        }

        return true;
    }
}
