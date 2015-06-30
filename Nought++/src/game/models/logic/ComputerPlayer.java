package game.models.logic;

import game.models.model.CellButton;
import game.models.model.Coordinate;
import game.models.model.Status;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/***
 * This is the brain of the computer. The computer extends player
 */
public class ComputerPlayer extends Player {

    private GridPaneLogic gridLogic;
    private Coordinate lastMove;

    /***
     * Constructors for a new player
     * @param controller The reference to the grid pane logic used in the main game.
     * The computer is to be able to make references to it in order to get right move
     */
    public ComputerPlayer(GridPaneLogic controller) {
        super("Computer", new Image("/game/resources/Robot-icon.png"));

        this.gridLogic = controller;
    }

    /***
     * Generates a random message to show player1 when the computer has won the game
     * @return a random message when the computer has won the game
     */
    public String getEndGameMessage(){

        //Array of words to pick from
        String words[] =  new String[] {"The computer has won this round. Step your game up!",
                "It looks like you've been beaten by a computer. How does that feel?",
                "Seems like the computer has won this round. Better luck next time",
                "Keep your eyes peeled next time. The computer has won this round!",
        };

        //Randomly selects a message to display when the player looses to the computer
        return words[new Random().nextInt(words.length)];
    }

    /**
     *
     * @return the last move played by the computer
     */
    public Coordinate getLastMove() {
        return lastMove;
    }

    /**
     *
     * @return the computers next move
     */
    public Coordinate getNextMove()  {
        Coordinate move;

        //If the none of the buttons on the grid pane have been clicked then
        if (gridLogic.noNodesClicked())
            move = getFirstMove();
        else
            move = nextMove();

        lastMove = move;

        return move;
    }

    /**
     * The internals for getting the computers next move
     * @return a coordinate of where the computer would like to move to
     */
    private Coordinate nextMove() {

        //Creates a clone of the grid pane from the grid pane controller instance
        GridPane gridPaneClone = cloneGridPane(gridLogic.getGridPane());
        int boardDimension = gridLogic.getDimension();
        int amountToWin = gridLogic.getAmountNeededToWin();

        //Creates a mini grid pane logic which it uses to try every possible move to see it can win
        GridPaneLogic tempLogic = new GridPaneLogic(gridPaneClone, boardDimension, amountToWin);

        //The list of coordinates that if the player moved to would win the game
        List<Coordinate> player1WinList = new ArrayList<>();
        List<Coordinate> computerWinList = new ArrayList<>();

        //A lists of the coordinates that have not been used up
        List<Coordinate> unusedCoordinates = tempLogic.getUnusedCoordinates();

        //Loops twice, it sets the player of each unused button to player 1 on the first iteration
        //and the Computer on the second iteration. On every time it sets a player to a button
        //It checks if by moving to that location would win the game, if it does then it adds the
        //Coordinate value to a list
        for (int player = 1; player <= 2; player++) {

            //This is the player to be used
            Player targetPlayer = player == 1 ? tempLogic.getPlayer1() : this;

            for (Coordinate c : unusedCoordinates) {
                CellButton btn = tempLogic.getCellButtonAt(c);

                //Associates a player to a button
                btn.setAttachedPlayer(targetPlayer);

                //Checks if the game is won by associating that to a specific button
                if (tempLogic.checkGameState().getGameStatus() == Status.GAME_WON) {

                    if (player == 1)
                        player1WinList.add(new Coordinate(c));
                    else
                        computerWinList.add(new Coordinate(c));

                    //This is here for debug purposes
                    System.out.printf("%s can win @ Col:%d Row:%d\n", targetPlayer.getName(), c.column(), c.row());
                }

                //Removes the association so it doesn't conflict with next operation
                btn.setAttachedPlayer(null);
            }
        }

        //The computer prioritises winning first
        if (computerWinList.size() > 0)
            return computerWinList.get(0);

        //If it can't win it tries to block the user
        if (player1WinList.size() > 0)
            return player1WinList.get(0);

        //If it can't win and can't block user then it gets a random move
        return getRandomMove();
    }

    /**
     *
     * @return a random move
     */
    private Coordinate getRandomMove()  {

        //Gets a list of all the coordinates that have not been used/played on
        List<Coordinate> validMoves = gridLogic.getUnusedCoordinates();

        int randomIndex = new Random().nextInt(validMoves.size());

        return validMoves.get(randomIndex);
    }

    /**
     * Custom cloning of a grid ane
     * @param gridPane The grid pane to clone
     * @return Cloned grid ane
     */
    private GridPane cloneGridPane(GridPane gridPane) {
        GridPane clone = new GridPane();

        for (int i = 0; i < gridPane.getColumnConstraints().size(); i++) {
            clone.getColumnConstraints().add(new ColumnConstraints());
            clone.getRowConstraints().add(new RowConstraints());
        }

        //Takes all the CellButtons from the old grid and adds it to the new one
        for (Node n : gridPane.getChildren()) {
            CellButton btn = (CellButton) n;

            CellButton cellBtn = new CellButton(btn.getCoordinate());

            cellBtn.setAttachedPlayer(btn.getAttachedPlayer());
            clone.add(cellBtn, cellBtn.getCoordinate().column(), cellBtn.getCoordinate().row());
        }

        return clone;
    }

    /**
     *
     * @return the first move's coordinate
     */
    private Coordinate getFirstMove() {

        int columnSize = gridLogic.getColumnSize();
        int rowSize = gridLogic.getRowSize();

        boolean isGridEven = columnSize % 2 == 0 && rowSize % 2 == 0;

        //shouldWe meaning, it will randomly decide if it should play it's first move in the middle
        //I used this because I wouldn't want the computer to always play the midpoint if the grid is empty and it's the computers turn
        boolean shouldWe = new Random().nextBoolean();

        //If the grid is not even then it means we can pick a perfect midpoint
        if (! isGridEven && columnSize == rowSize)
            return shouldWe ? new Coordinate(columnSize / 2, rowSize / 2) : getRandomMove();
        else
            return getRandomMove();
    }
}
