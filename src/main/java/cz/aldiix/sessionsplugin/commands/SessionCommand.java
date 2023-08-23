package cz.aldiix.sessionsplugin.commands;

import cz.aldiix.sessionsplugin.Config;
import cz.aldiix.sessionsplugin.Message;
import cz.aldiix.sessionsplugin.Variables;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

import static cz.aldiix.sessionsplugin.Config.*;
import static cz.aldiix.sessionsplugin.Main.plugin;
import static cz.aldiix.sessionsplugin.Message.Type.*;

public class SessionCommand implements CommandExecutor, TabCompleter {

    private String[] args;
    private Player player;
    private List<Invitation> invitedPlayers = new ArrayList<>();
    private class Invitation {
        public Invitation(Player invitedPlayer, int sessionId) {
            this.invitedPlayer = invitedPlayer;
            this.sessionId = sessionId;

            invitedPlayers.add(this);
        }

        public Player invitedPlayer;
        public int sessionId;
    }


    // Util methods
    private boolean checkIfPlayerHasSession() {
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

    private int getNextSectionIndex(ConfigurationSection section) {
        int size = section.getKeys(false).size();

        for (int i = 0; i < size; i++) {
            String n = String.valueOf(i);

            if(section.getString(n) == null) return Integer.parseInt(n);
        }

        return size;
    }

    private void addPlayerToSession(ConfigurationSection session, Player player) {
        if(session == null || player == null) {
            System.out.println("Session or player is null!");
            return;
        }

        ConfigurationSection players = session.getConfigurationSection("players");
        if(players == null) {
            System.out.println("No players in session.");
            return;
        }

        int newPlayerId = getNextSectionIndex(players);
        players.set(newPlayerId + ".name", player.getDisplayName());
        players.set(newPlayerId + ".role", "member");

        Config.save();



        // announce to every player in the session
        for (String key : players.getKeys(false)) {
            Player p = plugin.getServer().getPlayer(Objects.requireNonNull(players.getString(key + ".name")));
            Message.send(ANNOUNCE, p, Variables.Messages.userJoinedYourSession.replaceAll("%player%", player.getDisplayName()));
        }
    }



    // Main methods
    private void createSession() {
        String sessionName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String playerName = player.getDisplayName();

        if(sessionName.isEmpty()) sessionName = playerName + "'s session";

        // sessions query
        ConfigurationSection sessionsSection = config.getConfigurationSection("sessions");
        int nextSessionIndex = getNextSectionIndex(sessionsSection);

        // check if player is already in a session
        if(nextSessionIndex != 0 && checkIfPlayerHasSession()) {
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
        newPlayer1.set("role", "owner");

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
        int id = getPlayersSessionID();
        ConfigurationSection session = config.getConfigurationSection("sessions." + id);

        if(session == null) {
            Message.send(ERROR, player, Variables.Messages.playerIsNotInSessionLeaveError);
            return;
        }

        ConfigurationSection players = session.getConfigurationSection("players");
        int numberOfPlayersInSession = players.getKeys(false).size();



        // player deletion from the session
        for (String key : players.getKeys(false)) {
            if(Objects.equals(players.getString(key + ".name"), player.getDisplayName())) {
                players.set(key, null);
                break;
            }
        }

        numberOfPlayersInSession--;



        // delete session if no players in the session or change owner if another player is in the session
        if(numberOfPlayersInSession < 1) {
            config.set("sessions." + id, null);
        } else if(Objects.equals(session.getString("owner"), player.getDisplayName())) {
            List<Integer> playersInSessionId = new ArrayList<>();

            for (String key : players.getKeys(false)) {
                playersInSessionId.add(Integer.parseInt(key));
            }

            int randomIndex = new Random().nextInt(playersInSessionId.size());
            int selectedPlayerId = playersInSessionId.get(randomIndex);

            String newOwnerName = players.getString(selectedPlayerId + ".name");
            Player newOwner = plugin.getServer().getPlayer(newOwnerName);

            Message.send(NORMAL, newOwner, Variables.Messages.sessionOwnerLeftYouAreTheNewOwner.replaceAll("%owner%", player.getDisplayName()));
            session.set("owner", newOwnerName);
        }



        Config.save();
        Message.send(SUCCESS, player, Variables.Messages.successfullyLeftSession);
    }

    private void invitePlayer() {
        if(!checkIfPlayerHasSession()) {
            Message.send(ERROR, player, Variables.Messages.playerIsNotConnectedToAnySessionInviteError);
            return;
        } else {
            ConfigurationSection session = config.getConfigurationSection("sessions." + getPlayersSessionID());
            boolean ret = false;

            if(!Objects.equals(session.getString("owner"), player.getDisplayName())) {
                Message.send(ERROR, player, Variables.Messages.youreNotPermittedInviteError);
                ret = true;
            }

            if(ret) return;
        }

        if(args.length < 2) {
            Message.send(ERROR, player, Variables.Messages.noPlayerSpecifiedInviteError);
            return;
        }

        if(player.getDisplayName().equalsIgnoreCase(args[1])) {
            Message.send(ERROR, player, Variables.Messages.cantInviteYourselfToSession);
            return;
        }



        Player invitedPlayer = plugin.getServer().getPlayer(args[1]);
        if(invitedPlayer == null) {
            Message.send(ERROR, player, Variables.Messages.specifiedPlayerDoesntExistInviteError);
            return;
        }

        Message.send(ANNOUNCE, invitedPlayer, Variables.Messages.someoneHasInvitedYouToSession.replaceAll("%player%", player.getDisplayName()));
        String sessionName = config.getString("sessions." + getPlayersSessionID() + ".name");
        Invitation inv = new Invitation(invitedPlayer, getPlayersSessionID());

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            invitedPlayers.remove(inv);
        }, 1200);
    }

    private void inviteAccept() {
        Invitation selectedInvitation = invitedPlayers.stream()
        .filter(i -> i.invitedPlayer.getDisplayName().equals(player.getDisplayName()))
        .findFirst()
        .orElse(null);

        if(selectedInvitation == null) {
            // msg
            System.out.println("no found invitation");
            return;
        }



        // find session by id
        ConfigurationSection session = config.getConfigurationSection("sessions." + selectedInvitation.sessionId);
        Player invitedPlayer = selectedInvitation.invitedPlayer;

        if(session == null) {
            System.out.println("session not found err");
            return;
        }

        addPlayerToSession(session, invitedPlayer);
        Message.send(SUCCESS, player, Variables.Messages.sessionInviteAccepted);
    }

    private void inviteDeny() {
        Invitation selectedInvitation = invitedPlayers.stream()
                .filter(i -> i.invitedPlayer.getDisplayName().equals(player.getDisplayName()))
                .findFirst()
                .orElse(null);

        if(selectedInvitation == null) {
            // msg
            System.out.println("no found invitation");
            return;
        }

        invitedPlayers.remove(selectedInvitation);
        Message.send(SUCCESS, player, Variables.Messages.sessionInviteDeclined);
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
            case "invite" -> invitePlayer();
            case "inviteaccept" -> inviteAccept();
            case "invitedeny" -> inviteDeny();
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
        Player s = (Player) sender;

        boolean senderIsInvited = invitedPlayers.stream()
        .filter(i -> i.invitedPlayer.getDisplayName().equals(s.getDisplayName()))
        .findFirst()
        .orElse(null) != null;



        if (args.length == 1) {
            completions.add("create");
            completions.add("invite");
            completions.add("delete");
            completions.add("leave");
            if(senderIsInvited) {
                completions.add("inviteaccept");
                completions.add("invitedeny");
            }
        } else if(args.length == 2 && Objects.equals(args[0], "create")) {
            completions.add("[<name>]");
        } else if(args.length == 2 && Objects.equals(args[0], "invite")) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                completions.add(p.getDisplayName());
            }
        }

        return completions;
    }
}