package com.minecraft_among_us.plugin.game;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Vote system class.
 *
 * The vote system is the part of the game when players discuss and vote.
 */
public class Vote {

    private final AmongUsPlayer auCaller;
    private final boolean emergency;
    private int discussionTimeCooldown;
    private int votingTimeCooldown;

    /**
     * Creates a new vote system.
     *
     * @param auCaller Player who called the voting time (by emergency or discover dead body)
     * @param emergency True will starts the vote by emergency call, false will starts the vote by discovering dead body
     */
    public Vote(AmongUsPlayer auCaller, boolean emergency) {
        Game game = Game.getInstance();
        this.auCaller = auCaller;
        this.emergency = emergency;
        this.discussionTimeCooldown = game.getSettings().discussionTime;
        this.votingTimeCooldown = game.getSettings().votingTime;
    }

    /**
     * Starts the vote system.
     */
    public void start() {
        Game game = Game.getInstance();
        game.setState(GameState.VOTE);
        List<Location> emergencySpawns = ConfigurationManager.getInstance().mapEmergency;
        int i = 0;
        for (Player player : game.getPlayers().stream().filter(AmongUsPlayer::isAlive).map(AmongUsPlayer::toBukkitPlayer).map(OfflinePlayer::getPlayer).collect(Collectors.toList())) {
            player.teleport(emergencySpawns.get(i++));
            player.sendTitle(emergency ? "§cEmergency call" : "§cDead body found", emergency ? "§7Called by" : "§7Founded by" + " §6" + auCaller.toBukkitPlayer().getName(), 5, 80, 15);
            player.playSound(player.getLocation(), emergency ? Sound.ENTITY_PLAYER_LEVELUP : Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.AMBIENT, 1.0F, 0.0F);
            player.setWalkSpeed(0.0F);
            player.setFoodLevel(6);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 200, false, false, false));
        }
        this.startDiscussionTime();
    }

    /**
     * Stops the vote system.
     */
    public void stop() {
        Game game = Game.getInstance();
        game.setState(GameState.IN_PROGRESS);
        game.getVotingBar().removeAll();
        game.getPlayers().stream().filter(AmongUsPlayer::isAlive).map(AmongUsPlayer::toBukkitPlayer).map(OfflinePlayer::getPlayer).collect(Collectors.toList()).forEach(player -> {
            player.setWalkSpeed(0.2F);
            player.setFoodLevel(20);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.AMBIENT, 1.0F, 1.0F);
        });
    }

    /**
     * Starts the discussion time.
     */
    private void startDiscussionTime() {
        Game game = Game.getInstance();
        game.getVotingBar().setTitle("Discussion time");
        Bukkit.getOnlinePlayers().forEach(game.getVotingBar()::addPlayer);
        Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), task -> {
            if (this.discussionTimeCooldown > 0) {
                game.getVotingBar().setProgress((double) this.discussionTimeCooldown / (double) game.getSettings().discussionTime);
                this.discussionTimeCooldown--;
            } else {
                this.startVotingTime();
                task.cancel();
            }
        }, 0L, 20L);
    }

    /**
     * Starts the voting time.
     */
    private void startVotingTime() {
        Game game = Game.getInstance();
        game.getVotingBar().setTitle("Voting time");
        Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), votingTask -> {
            if (this.votingTimeCooldown <= 10) {
                game.getVotingBar().setColor(this.votingTimeCooldown % 2 == 0 ? BarColor.WHITE : BarColor.RED);
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.AMBIENT, 1.0F, 1.0F - ((float) this.votingTimeCooldown * 0.1F) + 1.0F));
            }
            if (this.votingTimeCooldown > 0) {
                game.getVotingBar().setProgress((double) this.votingTimeCooldown / (double) game.getSettings().votingTime);
                this.votingTimeCooldown--;
            } else {
                this.stop();
                votingTask.cancel();
            }
        }, 0L, 20L);
    }
}
