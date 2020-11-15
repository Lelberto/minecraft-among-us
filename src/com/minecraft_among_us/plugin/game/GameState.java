package com.minecraft_among_us.plugin.game;

/**
 * Game state enumeration.
 */
public enum GameState {
    HUB, IN_PROGRESS, VOTE_DISCUSSION, VOTE_PROGRESS, VOTE_END, FINISH;

    /**
     * Checks if the provided state is a vote state.
     *
     * Vote states are :
     * - {@link GameState#VOTE_DISCUSSION}
     * - {@link GameState#VOTE_PROGRESS}
     * - {@link GameState#VOTE_END}
     *
     * @param state State to check
     * @return True if the state is a vote state, false otherwise
     */
    public static boolean isVote(GameState state) {
        return state.equals(VOTE_DISCUSSION) || state.equals(VOTE_PROGRESS) || state.equals(VOTE_END);
    }
}
