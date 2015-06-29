package game.controllers;

import game.models.logic.Player;
import game.models.misc.AlertBox;
import game.models.misc.ButtonResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class HomeController implements Initializable {

    @FXML
    private TextField txtPlayerName;
    @FXML
    private ImageView imgViewPlayer;
    @FXML
    private Label lblQuit, lblRecentScores, lblAbout, lblNewGame;
    @FXML
    private RadioButton rdo3x3, rdo4x4;
    @FXML
    private Button btnStartGame;

    private static Player player1;
    private static int dimension = 3;
    private static Stage thisStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Assigning all the Mouse click event to the labels in the menu
        lblNewGame.setOnMouseClicked(e -> btnStartGame.fireEvent(new ActionEvent()));
        lblQuit.setOnMouseClicked(e -> ((Stage) lblQuit.getScene().getWindow()).close());
        lblRecentScores.setOnMouseClicked(e -> loadRecentGameScene());

        //Assigns the action event or the radio buttons
        rdo3x3.setOnAction(e -> dimension = 3);
        rdo4x4.setOnAction(e -> dimension = 4);

        //Creates a new tool tip for when the user hovers over the image view
        Tooltip.install(imgViewPlayer, new Tooltip("Right click for more options"));

        //Initializes the context menu for when the image view is clicked
        initializeContextMenu();
    }

    /***
     * Initializes the context menu for when the image view is clicked
     */
    private void initializeContextMenu() {

        ImageView imgViewChange = new ImageView(new Image("/game/resources/edit-20.png"));
        ImageView imgViewReset = new ImageView(new Image("/game/resources/refresh.png"));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemImgChange = new MenuItem("Change Image", imgViewChange);
        MenuItem itemImgReset = new MenuItem("Reset Image", imgViewReset);

        itemImgReset.setOnAction(e -> imgViewPlayer.setImage(new Image("/game/resources/chicken-icon.png")));
        itemImgChange.setOnAction(e -> promptImageChange());

        contextMenu.getItems().addAll(itemImgChange, itemImgReset);

        imgViewPlayer.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY)
                contextMenu.show(imgViewPlayer, e.getScreenX(), e.getScreenY());
            else
                contextMenu.hide();
        });
    }

    /**
     * Allows the user to change their players image
     */
    private void promptImageChange() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");

        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images", "*.png", "*.jpg"));

        File file = fileChooser.showOpenDialog(imgViewPlayer.getScene().getWindow());

        if (file != null) {
            Image img  = new Image("file:" + file.getPath());

            if (img.getHeight() > 128 || img.getWidth() > 128)
                AlertBox.show("Images larger than 128 x 128 are not allowed");
            else
                imgViewPlayer.setImage(img);
        }
    }

    /**
     * The action that happens when the user clicks the about label
     * @param event
     * @throws Exception
     */
    @FXML
    public void lblAbout_onMouseClicked(MouseEvent event) throws  Exception {
        //The code that is executed when the about label is clicked

        String msg = "Nought++ is an enhanced version of the traditional noughts and crosses game." +
                " Developed by +Haze in JavaFX \n\n\t\tWould you like to visit the GitHub page?";

        if (AlertBox.showConfirmDialog("Information", msg) == ButtonResult.YES)
            Desktop.getDesktop().browse(new URI("https://github.com/PlusHaze/NoughtPlusPlus"));
    }

    /**
     * Loads the recent scores scene
     */
    private void loadRecentGameScene() {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/game/views/sceneRecentScores.fxml"));

            Stage s = new Stage(StageStyle.UNIFIED);
            s.initModality(Modality.APPLICATION_MODAL);

            s.setScene(new Scene(root));
            s.centerOnScreen();

            s.setResizable(false);

            s.setTitle("Nought ++     Recent Scores");
            s.getIcons().add(new Image("/game/resources/Spaceship.png"));
            s.sizeToScene();
            s.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnStartGame_onAction(ActionEvent event) throws Exception {

        // Validates the users input

        if (txtPlayerName.getText().trim().toLowerCase().equals("computer")) {
            AlertBox.show("Sorry you can't choose this name");
            return;
        }

        if (txtPlayerName.getText().trim().isEmpty() || ! isValidName(txtPlayerName.getText())) {
            AlertBox.show("Name must consist only of alphabets and numbers [a-zA-Z0-9]" +
                    "\nName must also be less than 15 characters in length");
            return;
        }

        //If validation has been passed then initialize the Player object and load game scene
        player1 = new Player(txtPlayerName.getText().trim(), imgViewPlayer.getImage());
        loadGameScene();
    }

    /**
     * Validates the users name to see if it matches a certain pattern or allowed usernames
     * @param name The name to be evaluated
     * @return The boolean resultant of the operation
     */
    private boolean isValidName(String name) {
        return Pattern.matches("[a-zA-Z0-9 ]{1,15}", name);
    }

    /**
     * Loads the game scene where the game will commence
     * @throws Exception
     */
    private void loadGameScene() throws Exception {

        thisStage = ((Stage) txtPlayerName.getScene().getWindow());
        thisStage.hide();

        Parent root = FXMLLoader.load(getClass().getResource("/game/views/sceneGame.fxml"));

        Stage s = new Stage();
        s.setScene(new Scene(root));
        s.centerOnScreen();

        s.setResizable(false);

        s.setTitle("Nought ++");
        s.getIcons().add(new Image("/game/resources/Spaceship.png"));
        s.sizeToScene();

        s.show();
    }

    /**
     *
     * @return The Player 1 instance
     */
    public static Player getPlayer1() {
        return player1;
    }

    /**
     *
     * @return The chosen grid dimension the user has picked
     */
    public static int getDimension() {
       return dimension;
    }

    /**
     *
     * @return The current scene stage
     */
    public static Stage getStage() {
        return thisStage;
    }
}