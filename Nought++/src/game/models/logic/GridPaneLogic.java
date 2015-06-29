package game.models.logic;

import game.models.model.CellButton;
import game.models.model.Coordinate;
import game.models.model.GameState;
import game.models.model.Status;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all the logic of the grid pane view
 */
public class GridPaneLogic {

    private final GridPane GRID_PANE; //This grid pane that is associated with this object
    private final int GRID_DIMENSION; //The dimension the grid pane is to be
    private final int AMOUNT_NEEDED_TO_WIN; //The amount of buttons that have to be linked with the same player before the player can win
    private final List<Timeline> ANIMATION_TIMELINE; //An array of animation objects. This is used to animate the winning buttons

    /**
     *
     * @param gridPane The grid pane to associate this object with
     * @param gridDimension The dimension the grid pane should be
     * @param amountToWin The amount needed to be matched for a player to win the game
     */
    public GridPaneLogic(GridPane gridPane, int gridDimension, int amountToWin)  {

        this.GRID_PANE = gridPane;
        this.GRID_DIMENSION = gridDimension;
        this.AMOUNT_NEEDED_TO_WIN = amountToWin;

        this.ANIMATION_TIMELINE = new ArrayList<>();

        initializeGrid();
    }

    /**
     *
     * @return Board dimension
     */
    public int getDimension() {
        return this.GRID_DIMENSION;
    }

    /**
     * Gets the number of columns in the GirdPane
     * @return column count
     */
    public int getColumnSize() {
        return GRID_PANE.getColumnConstraints().size();
    }

    /**
     * Gets the number of rows in the GirdPane
     * @return row count
     */
    public int getRowSize() {
        return GRID_PANE.getRowConstraints().size();
    }

    /**
     *
     * @return Amount needed to win a game
     */
    public int getAmountNeededToWin() {
        return this.AMOUNT_NEEDED_TO_WIN;
    }

    /**
     * Gets the grid pane that was initialized in the constructor
     * @return the grid pane associated with this object
     */
    public GridPane getGridPane() {
        return this.GRID_PANE;
    }

    /**
     *
     * @return a reference to a non computer player which is Player1
     */
    public Player getPlayer1() {

        for (Coordinate c : getUsedCoordinates()) {
            Player player = getCellButtonAt(c).getAttachedPlayer();
            if (player != null && !(player instanceof ComputerPlayer))
                return player;
        }

        return null;
    }

    /**
     * Initializes the grid pane that was passed in the constructor
     */
    private void initializeGrid() {

        GRID_PANE.getColumnConstraints().clear();
        GRID_PANE.getRowConstraints().clear();

        GRID_PANE.setHgap(5);
        GRID_PANE.setVgap(5);

        GRID_PANE.setPadding(new Insets(5, 5, 5, 5));

        //Adds the columns and rows depending on the grid dimension
        for (int i = 0; i < GRID_DIMENSION; i++) {
            GRID_PANE.getColumnConstraints().add(new ColumnConstraints());
            GRID_PANE.getRowConstraints().add(new RowConstraints());
        }
    }

    /**
     * Initializes all the buttons in the grid pane and assigns an event to each
     * @param event The event to assign all the Cell buttons in the grid pane
     */
    public void initializeButtons(EventHandler<ActionEvent> event) {

        GRID_PANE.getChildren().clear();

        int rowCount = GRID_PANE.getRowConstraints().size();
        int columnCount = GRID_PANE.getColumnConstraints().size();

        double estimateButtonWidth = GRID_PANE.getWidth() / rowCount;
        double estimateButtonHeight = GRID_PANE.getHeight() / columnCount;

        for (int row = 0; row < rowCount; row++) {

            for (int column = 0; column < columnCount; column++) {

                //Creates an instance of the CustomBottom and adds them to the grid pane

                CellButton btn = new CellButton(new Coordinate(column, row));

                //TO get better understanding of the coordinates of the button use commented code below
                //CellButton btn = new CellButton(new Coordinate(column, row), "C: " + column + " R: " + row);

                btn.setStyle(" -fx-background-radius: 0 ");
                btn.setPrefSize(estimateButtonWidth, estimateButtonHeight);
                btn.setOnAction(event);

                //Adds button to grid
                GRID_PANE.add(btn, column, row);
            }
        }
    }

    /**
     * Checks if all buttons have been clicked but no one has won
     * @return boolean resultant of operation
     */
    private boolean isGameDrawn() {
        return GRID_PANE.getChildren().stream().noneMatch(n -> ((CellButton) n).getAttachedPlayer() == null);
    }

    /**
     * Performs a check on the grid pane on all directions
     * @return returns a tuple object(status, winning player, list(of coordinates)
     * of the result of checking the grid
     */
    public GameState checkGameState() {

        //This method checks if a player has won the gameLogic
        //The function returns GameTuple object which was an attempt of implementing a tuple
        //If the game is won it returns the coordinates of the buttons that match
        //It calls other methods which do individual checks

        GameState horizontalCheck = checkGridHorizontally();
        if (horizontalCheck.getGameStatus() == Status.GAME_WON)
            return horizontalCheck;

        GameState verticalCheck = checkGridVertical();
        if (verticalCheck.getGameStatus() == Status.GAME_WON)
            return verticalCheck;

        GameState diagonalCheck = checkGridDiagonally();
        if (diagonalCheck.getGameStatus() == Status.GAME_WON)
            return diagonalCheck;

        //if nothing is matched vertically or horizontally or diagonally
        //then if board is 3x3 then return diagonal check or return the result of checking the grid alternatively
        if (GRID_DIMENSION == 3)
            return diagonalCheck;
        else
            return check4x4Grid();
    }

    /**
     * Performs a check on the grid pane horizontally
     * @return returns a tuple object(status, winning player, list(of coordinates)
     * of the result of checking the grid horizontally
     */
    private GameState checkGridHorizontally() {
        List<Coordinate> winningCoordinates = new ArrayList<>();

        //Checks all the columns from a left to right direction by
        for (int row = 0; row < GRID_DIMENSION; row++) {

            int amountMatched = 0;
            winningCoordinates.clear();

            Player comparedPlayer = getCellButtonAt(0, row).getAttachedPlayer();

            for (int col = 0; col < GRID_DIMENSION - 1; col++) {

                Player currentPlayer = getCellButtonAt(col + 1, row).getAttachedPlayer();

                //if the previous cell button and current cell button were clicked by the same player
                //then increment amount matched.
                if (comparedPlayer != null && currentPlayer != null) {
                    if (comparedPlayer.equals(currentPlayer)) {

                        amountMatched++;
                        winningCoordinates.add(new Coordinate(col, row));
                        winningCoordinates.add(new Coordinate(col + 1, row));

                    } else {
                        amountMatched = 0;
                        winningCoordinates.clear();
                    }
                }

                comparedPlayer = currentPlayer;

                if (amountMatched == AMOUNT_NEEDED_TO_WIN - 1)
                    return new GameState(Status.GAME_WON, comparedPlayer, winningCoordinates);
            }
        }

        return new GameState(Status.GAME_ONGOING, null, null);
    }

    /**
     * Performs a check on the grid pane vertically
     * @return returns a tuple object(status, winning player, list(of coordinates)
     * of the result of checking the grid vertically
     */
    private GameState checkGridVertical() {
        List<Coordinate> winningCoordinates = new ArrayList<>();

        //Checks all the rows from a top to bottom direction..

        for (int col = 0; col < GRID_DIMENSION; col++) {

            int amountLinked = 0;
            winningCoordinates.clear();

            Player comparedPlayer = getCellButtonAt(col, 0).getAttachedPlayer();

            for (int row = 0; row < GRID_DIMENSION - 1; row++) {

                Player currentPlayer = getCellButtonAt(col, row + 1).getAttachedPlayer();

                if (comparedPlayer != null && currentPlayer != null) {
                    if (comparedPlayer.equals(currentPlayer)) {

                        amountLinked++;
                        winningCoordinates.add(new Coordinate(col, row));
                        winningCoordinates.add(new Coordinate(col, row +1));

                    } else {
                        amountLinked = 0;
                        winningCoordinates.clear();
                    }
                }

                comparedPlayer = currentPlayer;

                if (amountLinked == AMOUNT_NEEDED_TO_WIN - 1)
                    return new GameState(Status.GAME_WON, comparedPlayer, winningCoordinates);

            }
        }

        return new GameState(Status.GAME_ONGOING, null, null);
    }

    /**
     * Performs a check on the grid pane diagonally, checks the state of the game
     * @return a tuple object(status, winning player, list(of coordinates)
     */
    private GameState checkGridDiagonally() {
        List<Coordinate> winningCoordinates = new ArrayList<>();

        //Visually the check that is done in this method looks like the img below
        // http://sr.photos3.fotosearch.com/bthumb/CSP/CSP881/k8817997.jpg

        //Checking diagonally downwards in the right direction

        int amountLinked = 0;
        for (int i= 0; i < GRID_DIMENSION - 1; i++) {

            Player currentPlayer = getCellButtonAt(i, i).getAttachedPlayer();
            Player nextPlayer = getCellButtonAt(i + 1, i + 1).getAttachedPlayer();

            if (currentPlayer != null && nextPlayer != null) {

                if (currentPlayer.equals(nextPlayer)) {

                    amountLinked++;
                    winningCoordinates.add(new Coordinate(i, i));
                    winningCoordinates.add(new Coordinate(i + 1, i + 1));
                } else {

                    amountLinked = 0;
                    winningCoordinates.clear();
                }
            }

            if (amountLinked == AMOUNT_NEEDED_TO_WIN - 1)
                return new GameState(Status.GAME_WON, nextPlayer, winningCoordinates);
        }


        winningCoordinates.clear();
        //Checking diagonally upwards in the right direction

        int amountLinkedDiagonallyUp = 0;
        for (int col = 0; col < GRID_DIMENSION - 1; col++) {

            int tempRow = GRID_DIMENSION - col - 1;
            int tempCol = col;

            Player currentPlayer =  getCellButtonAt(tempCol, tempRow).getAttachedPlayer();
            Player nextPlayer =  getCellButtonAt(++tempCol, --tempRow).getAttachedPlayer();

            if (currentPlayer != null && nextPlayer != null) {
                if (currentPlayer.equals(nextPlayer)) {

                    amountLinkedDiagonallyUp++;

                    winningCoordinates.add(new Coordinate(tempCol, tempRow));
                    winningCoordinates.add(new Coordinate(--tempCol, ++tempRow));

                } else {
                    amountLinkedDiagonallyUp = 0;
                    winningCoordinates.clear();
                }
            }

            if (amountLinkedDiagonallyUp == AMOUNT_NEEDED_TO_WIN -1)
                return new GameState(Status.GAME_WON, currentPlayer, winningCoordinates);
        }

        return new GameState(isGameDrawn() ? Status.GAME_DRAWN : Status.GAME_ONGOING, null, null);
    }

    /**
     *  Performs a specific check on a 4x4 grid pane diagonally, checks the state of the game
     * @return a tuple object(status, winning player, list(of coordinates)
     */
    private GameState check4x4Grid() {
        List<Coordinate> winningCoordinates = new ArrayList<>();

        //The coordinates I intend to check, this is done manually and only works for a 4x4 grid
        //The method I originally wanted to implement was tricky to get working and was too clunky

        String diagonalCheck1 = "0:1,1:2,2:3";
        String diagonalCheck2 = "1:0,2:1,3:2";

        String diagonalCheck3 = "0:2,1:1,2:0";
        String diagonalCheck4 = "1:3,2:2,3:1";

        String[] allChecks = new String[]
                {diagonalCheck1, diagonalCheck2, diagonalCheck3, diagonalCheck4};

        for (String diagonalCheck : allChecks) {

            List<CellButton> buttonsList = new ArrayList<>();

            //Split each line by a comma therefore leaving items looking like 0:1
            //Each item representing a coordinate to check
            String coordinates[] = diagonalCheck.split(",");

            //Splitting and extracting the two numbers
            for (String chunk : coordinates) {
                String[] splitter = chunk.split(":");
                int col = Integer.valueOf(splitter[0]);
                int row = Integer.valueOf(splitter[1]);

                //Gets the button associated with each coordinate
                buttonsList.add(getCellButtonAt(col, row));
            }

            List<Player> playerList = new ArrayList<>();

            //Gets the player associated with each button and adds it to a list
            buttonsList.forEach(btn -> playerList.add(btn.getAttachedPlayer()));

            //Checks if all players for each button is null
            boolean isNull = playerList.stream().noneMatch(b -> b != null);

            //Checks the amount of players that are distinct
            List<Player> distinctList = playerList.stream().distinct().collect(Collectors.toList());

            //If all players are not null and the number of distinct players == 1 then that means that all
            //The players were the same, and therefore the player has succesfully connected 3 in a row
            if (!isNull && distinctList.size() == 1) {
                buttonsList.forEach(b -> winningCoordinates.add(b.getCoordinate()));
                return new GameState(Status.GAME_WON, buttonsList.get(0).getAttachedPlayer(), winningCoordinates);
            }
        }

        return new GameState(isGameDrawn() ? Status.GAME_DRAWN : Status.GAME_ONGOING, null, null);
    }

    /**
     * Checks if any of the buttons in the grid pane have been clicked
     * @return boolean resultant of operation
     */
    public boolean noNodesClicked() {

        //Checks if none of the buttons have been clicked yet!
        //If there is a button that has a player that is not null
        //then that means it has been clicked, therefore return false
        return GRID_PANE.getChildren().stream().noneMatch(n -> ((CellButton) n).getAttachedPlayer() != null);
    }

    /**
     * Returns CellButton at specific location
     * @param col The column location of the button
     * @param row The row location of the button
     * @return the CellButton that was found, it not found then null is returned
     */
    public CellButton getCellButtonAt(int col, int row)   {

        for (Node node : GRID_PANE.getChildren()) {
            CellButton btn = (CellButton) node;
            if (btn.getCoordinate().row() == row && btn.getCoordinate().column() == col)
                return btn;
        }

        //Button not found
        return null;
    }

    /**
     * Returns CellButton at specific location
     * @param coords The coordinate of the button to find
     * @return the CellButton that was found, it not found then null is returned
     */
    public CellButton getCellButtonAt(Coordinate coords) {
        return getCellButtonAt(coords.column(), coords.row());
    }

    /**
     * Gets a list of all the coordinates of the buttons that have been clicked
     * @return the list of coordinates found
     */
    private List<Coordinate> getUsedCoordinates() {
        List<Coordinate> coordinatesList = new ArrayList<>();

        //Gets all the buttons which a player has clicked
        GRID_PANE.getChildren().forEach(n -> {
            CellButton btn = (CellButton) n;
            if (btn.getAttachedPlayer() != null)
                coordinatesList.add(btn.getCoordinate());
        });

        return coordinatesList;
    }

    /**
     * Gets a list of all the coordinates of the buttons that have not been clicked
     * @return the list of coordinates found
     */
    public List<Coordinate> getUnusedCoordinates() {

        List<Coordinate> coordinatesList = new ArrayList<>();

        //Gets all the buttons which no player has clicked
        GRID_PANE.getChildren().forEach(n -> {
            CellButton btn = (CellButton) n;
            if (btn.getAttachedPlayer() == null)
                coordinatesList.add(btn.getCoordinate());
        });

        return coordinatesList;
    }

    /**
     * Animates a group of buttons by their coordinate values
     * @param coords The list of buttons at a coordinate to animate
     */
    public void animateButtons(List<Coordinate> coords) {

        ANIMATION_TIMELINE.clear();

        for (Coordinate c : coords) {
            CellButton btn = getCellButtonAt(c);

            Glow glow = new Glow();
            glow.setLevel(1.0);

            //Sets a glow effect to the button
            btn.getGraphic().setEffect(glow);

            //Creates a timeline animation object that loops 4 ever
            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setAutoReverse(true);

            //Defining of the different frames of the animation timeline
            KeyValue rotateValue = new KeyValue(btn.getGraphic().rotateProperty(), 20);
            KeyValue glowValue = new KeyValue(glow.levelProperty(), 0.1);
            KeyValue rotateValue2 = new KeyValue(btn.getGraphic().rotateProperty(), -20);

            KeyFrame rotateFrame = new KeyFrame(Duration.millis(200), rotateValue);
            KeyFrame glowFrame = new KeyFrame(Duration.millis(300), glowValue);
            KeyFrame rotateFrame2 = new KeyFrame(Duration.millis(400), rotateValue2);

            //Add all animation timelines to the timeline object and plays
            timeline.getKeyFrames().addAll(rotateFrame , glowFrame, rotateFrame2);
            timeline.play();

            //Add to global list of timelines. This is so, that the animation could later be paused
            //If the used pauses the game
            ANIMATION_TIMELINE.add(timeline);
        }
    }

    /**
     * Disables all the nodes under the Grid pane
     */
    public void disableAllNodes() {
        GRID_PANE.getChildren().forEach(n -> n.setDisable(true));
    }

    /**
     * Removes the action event for all the nodes so that the user can't click it whilst it's the computers turn
     */
    public void removeAllNodesActionEvent() {
        GRID_PANE.getChildren().forEach(n -> ((CellButton) n).setOnAction(null));
    }

    /**
     * Sets an action event to for when a node on the grid pane is clicked
     * @param event The event to occur when a node is clicked
     */
    public void setAllNodesActionEvent(EventHandler<ActionEvent> event) {
        GRID_PANE.getChildren().forEach(n -> ((CellButton) n).setOnAction(event));
    }

    /**
     * Pauses the grid by pausing all the current playing animations setting a blur effect
     */
    public void pauseGrid() {

        ANIMATION_TIMELINE.forEach(Animation::pause);

        GRID_PANE.setDisable(true);
        GRID_PANE.setEffect(new GaussianBlur(10.0));
    }

    /**
     * un pauses the grid by resuming the animations and removing blur effect
     */
    public void resumeGrid() {

        ANIMATION_TIMELINE.forEach(Animation::play);

        GRID_PANE.setDisable(false);
        GRID_PANE.setEffect(null);
    }
}

// <editor-fold desc="Unused check routine">

//I tried to get this to work but it wasn't and it looks difficult to maintain and to kno whats going on and to be honest
//I've had enough of getting it to work so I just resorted to a more easy but specific way of doing it. If you can get this to work
//Then that would be appreciated look at the check4x4Grid routine to see my alternate way of doing it.

/**
 * Performs a check on the grid pane diagonally on a grid larger than 3x3
 * @return returns a tuple object(status, winning player, list(of coordinates)
 * of the result of checking the grid diagonally
 */
    /*
    private GameState checkGridDiagonalAlternative() {

        List<Coordinate> winningCoordinates = new ArrayList<>();

        boolean checksFinished = false;
        boolean isDecrementing = false;
        int counter = 0, absoluteCounter = 0;

        do {

            absoluteCounter++;

            if (absoluteCounter == GRID_DIMENSION)
                isDecrementing = true;

            if (isDecrementing)
                counter--;
            else
                counter++;

            int amountLinked = 0;
            for (int i = 0; i <= counter - 2; i++) {

                int col, row;
                Player currentPlayer, nextPlayer;
                CellButton currentButton, nextButton;

                if (isDecrementing) {

                    //TODO fix this it doesn't work properly.
                    //TODO also make this work for all scenarios including 5x5 grids of 6x6 etc
                    row = (counter - i) + 1;
                    col = i + 1;

                    currentButton = getCellButtonAt(col, row);
                    nextButton = getCellButtonAt(++col, --row);

                    currentPlayer = currentButton.getAttachedPlayer();
                    nextPlayer =  nextButton.getAttachedPlayer();
                } else {
                    //This part works
                    row = (counter - i) - 1;
                    col = i;

                    currentButton = getCellButtonAt(i, row);
                    nextButton =  getCellButtonAt(++col, --row);

                    currentPlayer = currentButton.getAttachedPlayer();
                    nextPlayer =  nextButton.getAttachedPlayer();
                }

                if (currentPlayer != null && nextPlayer != null) {
                    if (currentPlayer.equals(nextPlayer)) {
                        amountLinked++;

                        winningCoordinates.add(new Coordinate(currentButton.getCoordinate()));
                        winningCoordinates.add(new Coordinate(nextButton.getCoordinate()));

                    } else {
                        amountLinked = 0;
                        winningCoordinates.clear();
                    }
                }

                if (amountLinked == AMOUNT_NEEDED_TO_WIN - 1)
                    return new GameState(Status.GAME_WON, nextPlayer, winningCoordinates);
            }

            //If the do loop has went from low to max, and max to low.
            if (absoluteCounter == GRID_DIMENSION * 2 - 1)
                checksFinished = true;

        } while (! checksFinished);

        return new GameState(isGameDrawn() ? Status.GAME_DRAWN : Status.GAME_ONGOING, null, null);
    }
    */

// </editor-fold>