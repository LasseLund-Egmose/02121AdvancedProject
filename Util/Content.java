package Util;

public class Content {

    /*
     * Misc
     */

    public static final String LOAD_GAME = "Load game";
    public static final String NEW_GAME = "New game";
    public static final String WELCOME = "Welcome to the game of checkers!";

    /*
     * Game descriptions
     */

    public static final String GAME_DESCRIPTION_SIMP_DAM = "This is the simplest version of checkers. " +
            "In this version each player has exactly one piece each, starting at opposite corners of the board. " +
            "However, in this version every checker piece acts as a king piece, meaning it is not locked to only moving forward.";

    public static final String GAME_DESCRIPTION_TWO_PLAYER = "This is the normal version of checkers for two players. " +
            "Every player has pieces on the three first rows of the board on their own respective side. " +
            "All pieces can only move forward, unless they become king pieces";

    public static final String GAME_DESCRIPTION_SINGLE_PLAYER = "This is the Single player version of checkers, where you play against an AI. " +
            "The rules are the same as for regular checkers.";

    public static final String GAME_DESCRIPTION_FLEXIBLE_KING_TWO_PLAYER = "This is the international version of checkers. For two players. " +
            "This version has the international rules meaning you can move the king pieces an arbitrary amount of spaces. ";

    public static final String GAME_DESCRIPTION_NONE = "Please select a game mode.";

}
