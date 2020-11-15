package com.minecraft_among_us.plugin.game;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Vote system class.
 *
 * The vote system is the part of the game when players discuss and vote.
 */
public class VoteSystem {

    private final AmongUsPlayer auCaller;
    private final List<Vote> votes;
    private final List<AmongUsPlayer> skipVotes;
    private final boolean emergency;
    private int discussionTimeCooldown;
    private int votingTimeCooldown;

    /**
     * Creates a new vote system.
     *
     * @param auCaller Player who called the voting time (by emergency or discover dead body)
     * @param emergency True will starts the vote by emergency call, false will starts the vote by discovering dead body
     */
    public VoteSystem(AmongUsPlayer auCaller, boolean emergency) {
        Game game = Game.getInstance();
        this.auCaller = auCaller;
        this.votes = new ArrayList<>();
        this.skipVotes = new ArrayList<>();
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
        game.setCurrentVoteSystem(this);
        List<Location> emergencySpawns = ConfigurationManager.getInstance().mapEmergency;
        int i = 0;
        for (AmongUsPlayer auPlayer : game.getPlayers().stream().filter(AmongUsPlayer::isAlive).collect(Collectors.toList())) {
            Player player = (Player) auPlayer.toBukkitPlayer();
            auPlayer.refreshBar();
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
        game.setCurrentVoteSystem(null);
        game.getVotingBar().removeAll();
        game.getPlayers().stream().filter(AmongUsPlayer::isAlive).forEach(auPlayer -> {
            Player player = (Player) auPlayer.toBukkitPlayer();
            auPlayer.refreshBar();
            player.setWalkSpeed(0.2F);
            player.setFoodLevel(20);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.AMBIENT, 1.0F, 1.0F);
        });
    }

    /**
     * Sets a vote.
     *
     * @param auVoter Voter
     * @param auVoted Voted, or {@code null} if skip vote
     */
    public void vote(AmongUsPlayer auVoter, AmongUsPlayer auVoted) {
        if (auVoted == null) {
            this.skipVotes.add(auVoter);
        } else {
            Vote vote = this.votes.stream().filter(currentVote -> currentVote.getVoted().equals(auVoted)).findFirst().orElse(null);
            if (vote == null) {
                vote = new Vote(auVoted);
                this.votes.add(vote);
            }
            vote.getVoters().add(auVoter);
        }
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
                this.discussionTimeCooldown = game.getSettings().discussionTime;
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
                this.votingTimeCooldown = game.getSettings().votingTime;
                this.stop();
                votingTask.cancel();
            }
        }, 0L, 20L);
    }

    /**
     * Gets the vote caller.
     *
     * @return Vote caller
     */
    public AmongUsPlayer getCaller() {
        return auCaller;
    }

    /**
     * Returns the skipped voters.
     *
     * @return Skipped voters
     */
    public List<AmongUsPlayer> getSkipVotes() {
        return skipVotes;
    }

    /**
     * Returns the votes.
     *
     * @return Votes
     */
    public List<Vote> getVotes() {
        return votes;
    }

    /**
     * Gets the player who have the most votes.
     *
     * In case of equality or majority skip, this method returns {@code null}.
     *
     * @return Player who have the most votes, or {@code null} in case of equality or majority skip
     */
    public AmongUsPlayer getResult() {
        Collections.sort(this.votes);
        Collections.reverse(this.votes);
        Bukkit.broadcastMessage(this.votes.toString());

        if (this.votes.isEmpty()) { // If no vote
            return null;
        }
        if (this.votes.size() == 1) { // If one vote
            if (this.votes.get(0).getVoters().size() <= this.skipVotes.size()) { // If skipped votes is greater
                return null;
            }
            return this.votes.get(0).getVoted();
        }
        if (this.votes.get(0).getVoters().size() == this.votes.get(1).getVoters().size()) { // If equality between two votes
            return null;
        }
        return this.votes.get(0).getVoted();
    }

    /**
     * Checks if a player has voted.
     *
     * @param auPlayer Player to check
     * @return True if the player has voted, false otherwise
     */
    public boolean hasVoted(AmongUsPlayer auPlayer) {
        return this.votes.stream().anyMatch(vote -> vote.getVoters().contains(auPlayer)) || this.skipVotes.contains(auPlayer);
    }
}
