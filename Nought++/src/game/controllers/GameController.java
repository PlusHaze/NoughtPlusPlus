package game.controllers;

import game.models.io.GameIOHelper;
import game.models.logic.ComputerPlayer;
import game.models.logic.GridPaneLogic;
import game.models.logic.MenuAnimation;
import game.models.logic.Player;
import game.models.misc.AlertBox;
import game.models.misc.ButtonResult;
import game.models.model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private GridPane gameGrid;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label lblPlayer1, lblPlayer2, lblScore, lblWhoseTurn;
    @FXML
    private ImageView imgPlayer1, imgPlayer2;

    private MenuAnimation menuAnimation;
    private Player player1, nextPlayersTurn;
    private ComputerPlayer computer;
    private GridPaneLogic gridLogic;
    private MediaPlayer gameWonSound;
    private boolean gamePaused;

    public void initialize(URL location, ResourceBundle resources) {

        //Initialized a class that contains all the logic for the grid
        //It passes a reference to the Grid Pane which is manipulated inside the class.
        gridLogic = new GridPaneLogic(gameGrid, HomeController.getDimension(), 3);

        installMenuAnimation(borderPane);

        //Defines the player objects
        player1 = new Player(HomeController.getPlayer1()); //Both computer and player are of type Player
        computer = new ComputerPlayer(gridLogic); //computer inherits player

        lblPlayer1.setText(player1.getName());
        lblPlayer2.setText(computer.getName());

        imgPlayer1.setImage(player1.getImage());
        imgPlayer2.setImage(computer.getImage());

        //The sound to play when a player has won the game
        Media media = new Media(getClass().getResource("/game/resources/game_won.mp3").toString());
        gameWonSound = new MediaPlayer(media);

        //Randomly decides whose turn it should be using ternary statement
        nextPlayersTurn = new Random().nextBoolean() ? player1 : computer;

        Platform.runLater(() -> {
            restartGame();

            //Saves game scores if player the closes window
            gameGrid.getScene().getWindow().setOnCloseRequest(e -> saveGameScores());
        });
    }

    /**
     * Setups of an animation effect for the left item in the boarder pane
     * @param bp The border pane to attach an animation to
     */
    private void installMenuAnimation(BorderPane bp) {
        menuAnimation = new MenuAnimation(bp, (VBox) bp.getLeft());

        menuAnimation.setOnAnimationShown(e -> pauseGame());
        menuAnimation.setOnAnimationHidden(e -> resumeGame());

        bp.setLeft(null);
    }

    private void resumeGame() {

        gamePaused = false;

        gridLogic.resumeGrid();

        Status status = gridLogic.checkGameState().getGameStatus();

        if (status == Status.GAME_WON || status == Status.GAME_DRAWN) {
            promptNewGame();
            return;
        }

        if (nextPlayersTurn == computer)
            computerPlayLastMove();
        else
            updateLabelDisplay(); //Show the non computer player its their turn
    }

    private void pauseGame() {
        gamePaused = true;

        gridLogic.pauseGrid();

        lblWhoseTurn.setText("Game paused...");
        lblWhoseTurn.setCursor(Cursor.DEFAULT);
        lblWhoseTurn.setOnMouseClicked(null);
    }

    private void updateLabelDisplay() {
        //Updates the labels to reflect the state of the gameLogic
        lblWhoseTurn.setText(nextPlayersTurn.getName() + " it's your turn..");
        lblScore.setText(String.format("  %d - %d  ", player1.getScore(), computer.getScore()));
    }

    private void promptNewGame() {

        lblWhoseTurn.setText("Game over! Click me to play again");
        lblWhoseTurn.setCursor(Cursor.HAND);

        lblWhoseTurn.setOnMouseClicked(e -> {
            restartGame();
            lblWhoseTurn.setCursor(Cursor.DEFAULT);
            lblWhoseTurn.setOnMouseClicked(null);
        });
    }

    private void restartGame() {

        if (gamePaused)
            return;

        //Removes the old buttons and re-initializes new buttons
        gridLogic.initializeButtons(this::cellButtons_onAction);

        //Updates who's turn it is and score
        updateLabelDisplay();

        if (nextPlayersTurn == computer)
            computerPlayTurn();
    }

    /**
     * This is the code that is executed when any of the cell buttons in the grid pane have been clicked
     * @param event
     */
    private void cellButtons_onAction(ActionEvent event) {

        if (gamePaused)
            return;

        //Gets the button clicked
        CellButton btn  = (CellButton) event.getSource();

        Player currentPlayer = nextPlayersTurn;

        //If there has been no player attached to the button
        if (btn.getAttachedPlayer() == null) {

            //Associate a player with a button. This also changes the image of the button to the image of the player
            //go to the setAttachedPlayer method to see how that operates
            btn.setAttachedPlayer(currentPlayer);

            //Alternates the players turn
            nextPlayersTurn = (currentPlayer == player1) ? computer : player1;

            //Checks if the gameLogic has been won or not. If won it returns a tuple containing information
            //Such as winning player and the coordinates of the buttons that have won

            GameState gameCheck = gridLogic.checkGameState();
            Player winningPlayer = gameCheck.getWinningPlayer();

            switch (gameCheck.getGameStatus()) {

                case GAME_WON:

                    gameWonSound.stop();
                    gameWonSound.play();

                    gridLogic.animateButtons(gameCheck.getWinningCoordinates());

                    gridLogic.disableAllNodes();

                    if (winningPlayer == player1) {
                        player1.setScore(player1.getScore() +1);

                        AlertBox.show("Nice one " + winningPlayer.getName() + ", you've won this round!" +
                                "\nAre you ready for the next?");
                    } else {
                        computer.setScore(computer.getScore() +1);
                        AlertBox.show(computer.getEndGameMessage());
                    }

                    //Updates the score and shows whose turn it is
                    updateLabelDisplay();
                    promptNewGame();

                    break;

                case GAME_DRAWN:

                    gridLogic.disableAllNodes();
                    promptNewGame();

                    break;

                case GAME_ONGOING:

                    //Means no one has won and the game is still ongoing
                    if (nextPlayersTurn == computer)
                        computerPlayTurn();

                    updateLabelDisplay();

                    break;
            }
        }
    }

    private void computerPlayTurn() {

        Runnable r = () -> {
            //To prevent a user from clicking a button whilst it's the computers move
            gridLogic.removeAllNodesActionEvent();

            Platform.runLater(() -> lblWhoseTurn.setText("Computer is thinking ...."));
            Coordinate coords = computer.getNextMove();

            //Computer appears to be thinking to give it a more realistic feel
            int sleepAmount = (new Random().nextInt(6) + 1)  * 100;

            try {
                Thread.sleep(sleepAmount);
            } catch (InterruptedException ignored) { }

            //Retrieves reference to the button it wishes to click via it's coordinates
            CellButton cellBtn = gridLogic.getCellButtonAt(coords);

            Platform.runLater(() -> {
                //Re-adds the action event for all the buttons
                gridLogic.setAllNodesActionEvent(this::cellButtons_onAction);

                //Clicks the button it has picked
                //It's never gonna be null but this is just to stop the compiler from complaining
                if (cellBtn != null)
                    cellBtn.fireEvent(new ActionEvent());
            });
        };

        new Thread(r).start();
    }

    private void computerPlayLastMove()   {

        //If the game is paused && it's the computers turn
        //then the computer still has a record of it's last move. When unpaused the computer plays that turn again
        Runnable r = () -> {

            try {

                Platform.runLater(() -> lblWhoseTurn.setText("Computer is thinking ...."));

                Thread.sleep(200);

                Coordinate coords = computer.getLastMove();

                CellButton btn = gridLogic.getCellButtonAt(coords);

                if (btn != null)
                    Platform.runLater(() -> btn.fireEvent(new ActionEvent()));

            } catch (InterruptedException e) {
                AlertBox.show("Oops! Seems like the computer has died! Report this bug.");
            }
        };

        new Thread(r).start();
    }

    @FXML
    private void btnPause_onAction(ActionEvent event) {
        //if the menu bar is showing, it plays a hide animation, if it's hidden it plays a show animation
        menuAnimation.autoAnimateSidebar();
    }

    @FXML
    private void lblHome_onMouseClicked(MouseEvent event) throws Exception {

        //Saves scores
        saveGameScores();

        //Closes current stage
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        //Shows a new one
        HomeController.getStage().show();
    }

    private void saveGameScores() {
        //Doesn't save score if both players have 0 as their score
        if (! (player1.getScore() == 0 && computer.getScore() == 0))
            GameIOHelper.saveGameScores(new RecentScore(player1, computer, HomeController.getDimension()));
    }

    @FXML
    private void lblAbout_onMouseClicked(MouseEvent event) throws Exception {
        //The code that is executed when the about label is clicked

        String msg = "Nought++ is an enhanced version of the traditional noughts and crosses game." +
                " Developed by +Haze in JavaFX \n\n\t\tWould you like to visit the GitHub page?";

        if (AlertBox.showConfirmDialog("Information", msg) == ButtonResult.YES)
            Desktop.getDesktop().browse(new URI("https://github.com/PlusHaze/NoughtPlusPlus"));
    }

    @FXML
    private void lblQuit_onMouseClicked(MouseEvent event) {
        //Closes the current stage
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
