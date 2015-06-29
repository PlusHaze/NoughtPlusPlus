package game.controllers;

import game.models.misc.AlertBox;
import game.models.io.GameIOHelper;
import game.models.model.RecentScore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RecentScoresController implements Initializable {

    @FXML
    private Button btnReset;
    @FXML
    private TableColumn<RecentScore, String> tblScores, tblNames, tblDate, tblSize;
    @FXML
    private TableView<RecentScore> tableScores;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Initializing the columns first
        tblScores.setCellValueFactory(new PropertyValueFactory<>("scores"));
        tblNames.setCellValueFactory(new PropertyValueFactory<>("names"));
        tblDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tblSize.setCellValueFactory(new PropertyValueFactory<>("FormatDimension"));

        loadScores();
    }

    /**
     * Loads the recent scores of the user and displays them in a table view
     */
    private void loadScores() {
        try {
            List<RecentScore> recentScores = GameIOHelper.loadRecentScores();

            if (recentScores != null)
                tableScores.getItems().addAll(recentScores);

        } catch (IOException e) {
            AlertBox.show("Error @ " + e.getMessage());
        }
    }

    /**
     * Resets and deletes the users recent scores when the reset button is clicked
     * @param event
     */
    @FXML
    private void btnReset_onAction(ActionEvent event) {
        try {
            GameIOHelper.deleteRecentScores();
            tableScores.getItems().clear();

            AlertBox.show("All scores have been reset");
        } catch (IOException e) {
            AlertBox.show(e.getMessage());
        }
    }
}