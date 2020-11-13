package com.minecraft_among_us.plugin.game;

/**
 * Game settings class.
 */
public class GameSettings {

    public int impostors;
    public boolean confirmEjects;
    public int emergencyMeetings;
    public double emergencyCooldown;
    public int discussionTime;
    public int votingTime;
    public double killCooldown;
    public int commonTasks;
    public int longTasks;
    public int shortTasks;

    /**
     * Creates a new game settings.
     */
    protected GameSettings() {
        recommended(0);
    }

    /**
     * Sets the settings to recommended in terms of player count.
     *
     * @param playerCount Player count
     */
    public void recommended(int playerCount) {
        switch (playerCount) {
            default:
            case 4:
                impostors = 1;
                confirmEjects = true;
                emergencyMeetings = 1;
                emergencyCooldown = 15.0;
                discussionTime = 15;
                votingTime = 120;
                killCooldown = 35.0;
                commonTasks = 1;
                longTasks = 1;
                shortTasks = 2;
                break;
            case 5:
                impostors = 1;
                confirmEjects = true;
                emergencyMeetings = 1;
                emergencyCooldown = 15.0;
                discussionTime = 15;
                votingTime = 120;
                killCooldown = 30.0;
                commonTasks = 1;
                longTasks = 1;
                shortTasks = 2;
                break;
            case 6:
                impostors = 1;
                confirmEjects = true;
                emergencyMeetings = 1;
                emergencyCooldown = 15.0;
                discussionTime = 15;
                votingTime = 120;
                killCooldown = 25.0;
                commonTasks = 1;
                longTasks = 1;
                shortTasks = 2;
                break;
            case 7:
                impostors = 2;
                confirmEjects = true;
                emergencyMeetings = 1;
                emergencyCooldown = 15.0;
                discussionTime = 15;
                votingTime = 120;
                killCooldown = 35.0;
                commonTasks = 1;
                longTasks = 1;
                shortTasks = 2;
                break;
            case 8:
                impostors = 2;
                confirmEjects = true;
                emergencyMeetings = 1;
                emergencyCooldown = 15.0;
                discussionTime = 15;
                votingTime = 120;
                killCooldown = 30.0;
                commonTasks = 1;
                longTasks = 1;
                shortTasks = 2;
                break;
            case 9:
                impostors = 2;
                confirmEjects = true;
                emergencyMeetings = 1;
                emergencyCooldown = 15.0;
                discussionTime = 15;
                votingTime = 120;
                killCooldown = 25.0;
                commonTasks = 1;
                longTasks = 1;
                shortTasks = 2;
                break;
            case 10:
                impostors = 2;
                confirmEjects = true;
                emergencyMeetings = 1;
                emergencyCooldown = 15.0;
                discussionTime = 15;
                votingTime = 120;
                killCooldown = 20.0;
                commonTasks = 1;
                longTasks = 1;
                shortTasks = 2;
                break;
        }
    }
}
