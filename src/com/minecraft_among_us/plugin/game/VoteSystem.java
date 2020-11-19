package com.minecraft_among_us.plugin.game;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Vote system class.
 *
 * The vote system is the part of the game when players discuss and vote.
 */
public class VoteSystem {

    private static final int END_TIME_COOLDOWN = 5;

    private final AmongUsPlayer auCaller;
    private final List<Vote> votes;
    private final List<AmongUsPlayer> skipVotes;
    private final boolean emergency;
    private int discussionTimeCooldown;
    private int votingTimeCooldown;
    private int endTimeCooldown;

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
        this.endTimeCooldown = VoteSystem.END_TIME_COOLDOWN;
    }

    /**
     * Starts the vote system.
     */
    public void start() {
        Game game = Game.getInstance();
        Plugin.getDefaultWorld().getEntities().stream().filter(entity -> entity.hasMetadata("dead_body")).forEach(Entity::remove);
        game.setState(GameState.VOTE_DISCUSSION);
        game.setCurrentVoteSystem(this);
        List<Location> emergencySpawns = ConfigurationManager.getInstance().mapEmergency;
        int i = 0;
        for (AmongUsPlayer auPlayer : game.getPlayers()) {
            Player player = (Player) auPlayer.toBukkitPlayer();
            player.teleport(emergencySpawns.get(i++));
            player.sendTitle(emergency ? "§cEmergency call" : "§cDead body founded", (emergency ? "§7Called by" : "§7Founded by") + " §6" + auCaller.toBukkitPlayer().getName(), 5, 80, 15);
            player.playSound(player.getLocation(), emergency ? Sound.ENTITY_PLAYER_LEVELUP : Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.AMBIENT, 1.0F, 0.0F);
            auPlayer.setCurrentVent(null);
            auPlayer.refresh();
        }
        this.startDiscussionTime();
    }

    /**
     * Stops the vote system.
     */
    public void stop() {
        Game game = Game.getInstance();
        Random rand = new Random();
        List<Location> mapEmergency = ConfigurationManager.getInstance().mapEmergency;
        game.setState(GameState.IN_PROGRESS);
        game.setCurrentVoteSystem(null);
        game.getVotingBar().removeAll();
        game.getPlayers().forEach(auPlayer -> {
            Player player = (Player) auPlayer.toBukkitPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.AMBIENT, 1.0F, 1.0F);
            if (!auPlayer.isAlive()) {
                player.teleport(mapEmergency.get(rand.nextInt(mapEmergency.size())));
            }
            auPlayer.refresh();
        });

        AmongUsPlayer auEjected = this.getResult();
        if (auEjected != null) {
            Player ejected = (Player) auEjected.toBukkitPlayer();
            ejected.setGameMode(GameMode.SPECTATOR);
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(ejected.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 0.5F));
            ejected.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, ejected.getLocation(), 100, 0.8, 0.8, 0.8, 0.5);
            ejected.getWorld().spawnParticle(Particle.FLAME, ejected.getLocation(), 1000, 0.3, 0.3, 0.3, 0.1);
            auEjected.setAlive(false);
            auEjected.refresh();
            game.checkEndGame();
        }
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
        game.setState(GameState.VOTE_PROGRESS);
        game.getVotingBar().setTitle("Voting time");
        Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), votingTask -> {
            if (this.votingTimeCooldown <= 10) {
                game.getVotingBar().setColor(this.votingTimeCooldown % 2 == 0 ? BarColor.WHITE : BarColor.RED);
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.AMBIENT, 1.0F, 1.0F - ((float) this.votingTimeCooldown * 0.1F) + 1.0F));
            }
            if (this.votingTimeCooldown > 0) {
                if (game.getPlayers().stream().filter(AmongUsPlayer::isAlive).allMatch(this::hasVoted)) {
                    this.votingTimeCooldown = 1;
                }
                game.getVotingBar().setProgress((double) this.votingTimeCooldown / (double) game.getSettings().votingTime);
                this.votingTimeCooldown--;
            } else {
                this.votingTimeCooldown = game.getSettings().votingTime;
                this.startEndTime();
                votingTask.cancel();
            }
        }, 0L, 20L);
    }

    /**
     * Starts the end time.
     */
    private void startEndTime() {
        Game game = Game.getInstance();
        game.setState(GameState.VOTE_END);

        StringBuilder sb = new StringBuilder(Plugin.getPluginNameChat()).append("§lVote results");
        if (this.votes.isEmpty()) {
            sb.append("\n§cNo votes");
        } else {
            this.votes.forEach(vote -> {
                AmongUsPlayer auVoted = vote.getVoted();
                sb.append("\n§7- ").append(auVoted.getColor().code).append(auVoted.toBukkitPlayer().getName()).append("§r :");
                vote.getVoters().forEach(auVoter -> sb.append(" ").append(auVoter.getColor().code).append("▉"));
            });
        }
        if (this.skipVotes.isEmpty()) {
            sb.append("\n§cNo skip votes");
        } else {
            sb.append("\n§7§i- Skip votes :");
            this.skipVotes.forEach(auSkipVoter -> {
                sb.append(" ").append(auSkipVoter.getColor().code).append("▉");
            });
        }
        Bukkit.broadcastMessage(sb.toString());

        game.getVotingBar().setTitle("End voting time");
        Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), (task) -> {
            if (this.endTimeCooldown > 0) {
                game.getVotingBar().setProgress((double) this.endTimeCooldown / (double) VoteSystem.END_TIME_COOLDOWN);
                this.endTimeCooldown--;
            } else {
                this.endTimeCooldown = VoteSystem.END_TIME_COOLDOWN;
                this.stop();
                task.cancel();
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
