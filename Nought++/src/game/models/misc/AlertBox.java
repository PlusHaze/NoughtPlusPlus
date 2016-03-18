package game.models.misc;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.util.Optional;

public class AlertBox {

    /***
     * Returns a default model that all alert dialogs consist of
     * @param title The text to set the Alert box to
     * @param headerText The header text to set the Alert box to
     * @param contentText The content text to set the Alert box to
     * @return the alert dialog instance
     */
    private static Alert getDefaultAlertBox(String title, String headerText, String contentText) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);

        return alert;
    }

    /**
     * Displays an alert box
     * @param msg The message to display on the body of the alert box
     */
    public static void show(String msg) {
        getDefaultAlertBox("Alert", " ", msg).showAndWait();
    }

    /**
     * Displays a dialog with a confirmation dialogue
     * @param headerText The header text to display
     * @param msg The message body to display
     * @return The Button result
     */
    public static ButtonResult showConfirmDialog(String headerText, String msg) {

        Alert alert = getDefaultAlertBox("Alert", headerText, msg);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == buttonTypeYes ? ButtonResult.YES : ButtonResult.NO;
    }
}
