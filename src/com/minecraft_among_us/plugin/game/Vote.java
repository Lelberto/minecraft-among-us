package com.minecraft_among_us.plugin.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Vote class.
 *
 * This class represents a vote for a player with a list of voters.
 */
public class Vote implements Comparable<Vote> {

    private final AmongUsPlayer auVoted;
    private final List<AmongUsPlayer> auVoters;

    /**
     * Creates a new vote.
     *
     * @param auVoted Voted player
     */
    public Vote(AmongUsPlayer auVoted) {
        this.auVoted = auVoted;
        this.auVoters = new ArrayList<>();
    }

    /**
     * Gets the voted player.
     *
     * @return Voted player
     */
    public AmongUsPlayer getVoted() {
        return auVoted;
    }

    /**
     * Gets the list of voters.
     *
     * @return List of voters
     */
    public List<AmongUsPlayer> getVoters() {
        return auVoters;
    }

    @Override
    public int compareTo(Vote anotherVote) {
        return this.auVoters.size() - anotherVote.auVoters.size();
    }

    @Override
    public String toString() {
        return "Vote{" +
                "auVoted=" + auVoted +
                ", auVoters=" + auVoters +
                '}';
    }
}
