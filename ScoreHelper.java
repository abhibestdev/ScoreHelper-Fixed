package me.abhi.practice.util.misc;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author crisdev333
 */
public class ScoreHelper {

    private static HashMap<UUID, ScoreHelper> players = new HashMap<>();

    public static boolean hasScore(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public static ScoreHelper createScore(Player player) {
        return new ScoreHelper(player);
    }

    public static ScoreHelper getByPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public static ScoreHelper removeScore(Player player) {
        return players.remove(player.getUniqueId());
    }

    private Scoreboard scoreboard;
    private Objective sidebar;
    private Team enemy;
    private Team friendly;

    private ScoreHelper(Player player) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        // Create Teams

        enemy = scoreboard.registerNewTeam("enemy");
        enemy.setPrefix(CC.RED + "");

        friendly = scoreboard.registerNewTeam("friendly");
        friendly.setPrefix(CC.GREEN + "");

        for (int i = 1; i <= 15; i++) {
            Team team = scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(genEntry(i));
        }
        player.setScoreboard(scoreboard);
        players.put(player.getUniqueId(), this);
    }

    public void setTitle(String title) {
        title = ChatColor.translateAlternateColorCodes('&', title.replace("%splitter%", "┃"));
        sidebar.setDisplayName(title.length() > 32 ? title.substring(0, 32) : title);
    }

    public void setSlot(int slot, String text) {
        Team team = scoreboard.getTeam("SLOT_" + slot);
        String entry = genEntry(slot);
        if (!scoreboard.getEntries().contains(entry)) {
            sidebar.getScore(entry).setScore(slot);
        }

        text = ChatColor.translateAlternateColorCodes('&', text);
        String pre = split(text)[0];
        String suf = split(text)[1];
        team.setPrefix(pre);
        team.setSuffix(suf == null ? "" : suf);

    }

    public void removeSlot(int slot) {
        String entry = genEntry(slot);
        if (scoreboard.getEntries().contains(entry)) {
            scoreboard.resetScores(entry);
        }
    }

    public void setSlotsFromList(List<String> list) {
        while (list.size() > 15) {
            list.remove(list.size() - 1);
        }

        int slot = list.size();

        if (slot < 15) {
            for (int i = (slot + 1); i <= 15; i++) {
                removeSlot(i);
            }
        }

        for (String line : list) {
            setSlot(slot, line.replace("%splitter%", "┃"));
            slot--;
        }
    }

    private String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String s) {
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    private String getSecondSplit(String s) {
        if (s.length() > 32) {
            s = s.substring(0, 32);
        }
        return s.length() > 16 ? s.substring(16) : "";
    }

    private String[] split(String text) {
        String[] splitted = new String[2];
        if (text.length() <= 16) {
            splitted[0] = text;
        } else {
            String prefix = text.substring(0, 16), suffix = "";

            if (prefix.endsWith("\u00a7")) {
                prefix = prefix.substring(0, prefix.length() - 1);
                suffix = "\u00a7" + suffix;
            }

            suffix = StringUtils.left(ChatColor.getLastColors(prefix) + suffix + text.substring(16), 16);
            splitted[0] = prefix;
            splitted[1] = suffix;
        }
        return splitted;
    }

    public void addFriendly(Player player) {
        friendly.addEntry(player.getName());
    }

    public void addEnemy(Player player) {
        enemy.addEntry(player.getName());
    }

    public void resetFriendly() {
        friendly.getEntries().stream().forEach(e -> friendly.removeEntry(e));
    }

    public void resetEnemy() {
        enemy.getEntries().stream().forEach(e -> enemy.removeEntry(e));
    }

}