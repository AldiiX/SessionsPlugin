package cz.aldiix.sessionsplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import static cz.aldiix.sessionsplugin.SessionsPlugin.plugin;

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
        if(config.get("sessions") == null) config.set("sessions", new LinkedHashMap<>());
        if(config.get("messages.sessionSuccessfullyCreated") == null) config.set("messages.sessionSuccessfullyCreated", "The session has been successfully created.");
        if(config.get("messages.playerIsAlreadyConnectedToSessionError") == null) config.set("messages.playerIsAlreadyConnectedToSessionError", "You can't create a session because you're currently in one.");
        if(config.get("messages.playerIsNotOwnerSessionDeleteError") == null) config.set("messages.playerIsNotOwnerSessionDeleteError", "You can't delete this session, because you're not the owner of the session.");
        if(config.get("messages.sessionSuccessfullyDeleted") == null) config.set("messages.sessionSuccessfullyDeleted", "The session has been successfully deleted.");
        if(config.get("messages.successfullyLeftSession") == null) config.set("messages.successfullyLeftSession", "You've successfully left the session.");
        if(config.get("messages.playerIsNotInSessionLeaveError") == null) config.set("messages.playerIsNotInSessionLeaveError", "You're not in any session, so you can't leave one.");
        if(config.get("messages.sessionOwnerLeftYouAreTheNewOwner") == null) config.set("messages.sessionOwnerLeftYouAreTheNewOwner", "The owner of this session (%owner%) has left, you are the new owner.");
        if(config.get("messages.noPlayerSpecifiedInviteError") == null) config.set("messages.noPlayerSpecifiedInviteError", "You didn't specify user.\nUsage: §6/session invite <username>§c.");
        if(config.get("messages.specifiedPlayerDoesntExistInviteError") == null) config.set("messages.specifiedPlayerDoesntExistInviteError", "Specified user doesn't exist or is not connected to the server.");
        if(config.get("messages.someoneHasInvitedYouToSession") == null) config.set("messages.someoneHasInvitedYouToSession", "%player% has invited you to a session.\nType §6/session accept§e to accept the invite\nor §6/session deny§e to decline it.");
        if(config.get("messages.youreNotPermittedInviteError") == null) config.set("messages.youreNotPermittedInviteError", "You're not permitted to invite players to your session. Only owner can invite players.");
        if(config.get("messages.sessionInviteDeclined") == null) config.set("messages.sessionInviteDeclined", "You've successfully declined an invite.");
        if(config.get("messages.sessionInviteAccepted") == null) config.set("messages.sessionInviteAccepted", "You've successfully accepted an invite.");
        if(config.get("messages.userJoinedYourSession") == null) config.set("messages.userJoinedYourSession", "%player% joined your session.");
        if(config.get("messages.cantInviteYourselfToSession") == null) config.set("messages.cantInviteYourselfToSession", "You can't invite yourself to your session.");
        if(config.get("messages.playerIsNotConnectedToAnySessionInviteError") == null) config.set("messages.playerIsNotConnectedToAnySessionInviteError", "You are not connected to any session.");

        Variables.pluginPrefix = config.getString("prefix");
        Variables.Messages.sessionSuccessfullyCreated = config.getString("messages.sessionSuccessfullyCreated");
        Variables.Messages.playerIsAlreadyConnectedToSessionError = config.getString("messages.playerIsAlreadyConnectedToSessionError");
        Variables.Messages.playerIsNotOwnerSessionDeleteError = config.getString("messages.playerIsNotOwnerSessionDeleteError");
        Variables.Messages.sessionSuccessfullyDeleted = config.getString("messages.sessionSuccessfullyDeleted");
        Variables.Messages.successfullyLeftSession = config.getString("messages.successfullyLeftSession");
        Variables.Messages.playerIsNotInSessionLeaveError = config.getString("messages.playerIsNotInSessionLeaveError");
        Variables.Messages.sessionOwnerLeftYouAreTheNewOwner = config.getString("messages.sessionOwnerLeftYouAreTheNewOwner");
        Variables.Messages.noPlayerSpecifiedInviteError = config.getString("messages.noPlayerSpecifiedInviteError");
        Variables.Messages.specifiedPlayerDoesntExistInviteError = config.getString("messages.specifiedPlayerDoesntExistInviteError");
        Variables.Messages.someoneHasInvitedYouToSession = config.getString("messages.someoneHasInvitedYouToSession");
        Variables.Messages.youreNotPermittedInviteError = config.getString("messages.youreNotPermittedInviteError");
        Variables.Messages.sessionInviteDeclined = config.getString("messages.sessionInviteDeclined");
        Variables.Messages.sessionInviteAccepted = config.getString("messages.sessionInviteAccepted");
        Variables.Messages.userJoinedYourSession = config.getString("messages.userJoinedYourSession");
        Variables.Messages.cantInviteYourselfToSession = config.getString("messages.cantInviteYourselfToSession");
        Variables.Messages.playerIsNotConnectedToAnySessionInviteError = config.getString("messages.playerIsNotConnectedToAnySessionInviteError");
        save();
    }
}
