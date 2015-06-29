package game.models.model;

import game.models.logic.Player;

import java.util.List;

/**
 * An implementation of a Tuple
 */
public class GameState {

    private Status gameStatus;
    private Player player;
    private List <Coordinate> coordinateList;

    /**
     *
     * @param gameStatus The status of the Game
     * @param player The player that has won the game if State == Status.Game_WON
     * @param coordinateList The list of the winning coordinates
     */
    public GameState (Status gameStatus, Player player, List coordinateList) {
        this.gameStatus = gameStatus;
        this.player = player;
        this.coordinateList = coordinateList;
    }

    /**
     *
     * @return the status of the game
     */
    public Status getGameStatus() {
        return this.gameStatus;
    }

    /**
     *
     * @return the player that has won the game
     */
    public Player getWinningPlayer() {
        return this.player;
    }

    /**
     *
     * @return a list of the coordinates that have won
     */
    public List <Coordinate> getWinningCoordinates() {
        return coordinateList;
    }
}