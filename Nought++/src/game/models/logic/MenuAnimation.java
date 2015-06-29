package game.models.logic;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MenuAnimation {

    private Timeline showAnimation, hideAnimation;
    private EventHandler<ActionEvent> onShowCallBack, onHideCallBack;
    private BorderPane bPane;
    private VBox vBoxy;
    private double nodePrefWidth;
    private boolean isPlaying;

    /**
     *
     * @param p The borderPane that contains the VBox
     * @param vb The A reference to the Vbox to animate
     */
    public MenuAnimation(BorderPane p, VBox vb) {
        this.bPane = p;
        this.vBoxy = vb;
        this.nodePrefWidth = vBoxy.getPrefWidth();
        this.isPlaying = false;

        //Setting up a timeline object to handle the show animation
        showAnimation = new Timeline();
        showAnimation.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(vBoxy.prefWidthProperty(), 0)),
                new KeyFrame(Duration.millis(350.0), new KeyValue(vBoxy.prefWidthProperty(), nodePrefWidth)));

        showAnimation.setOnFinished(e -> isPlaying = false);

        //Setting up a timeline object to handle the hide animation
        hideAnimation = new Timeline();
        hideAnimation.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(vBoxy.prefWidthProperty(), nodePrefWidth)),
                new KeyFrame(Duration.millis(350.0), new KeyValue(vBoxy.prefWidthProperty(), 0)));

        hideAnimation.setOnFinished(e -> {
            bPane.setLeft(null);
            isPlaying = false;
        });
    }

    /**
     *
     * @param action The method to be called once the show animation has finished playing
     */
    public void setOnAnimationShown(EventHandler<ActionEvent> action) {
        onShowCallBack = action;
    }

    /**
     *
     * @param action The method to be called once the hide animation has finished playing
     */
    public void setOnAnimationHidden(EventHandler<ActionEvent> action) {
        onHideCallBack = action;
    }

    /**
     * This will play the correct animation depending if the menu is showing or not
     */
    public void autoAnimateSidebar() {

        if (isPlaying)
            return;

        isPlaying = true;

        if (bPane.getLeft() == null)
            playShowAnimation();
        else
            playHideAnimation();
    }

    /**
     * Plays the showing animation
     */
    private void playShowAnimation() {

        //Sets the content of the boarderPane to the VBox reference
        bPane.setLeft(vBoxy);

        //Then plays show animation
        showAnimation.play();

        if (onShowCallBack != null)
            onShowCallBack.handle(new ActionEvent());
    }

    /**
     * Plays the hiding animation
     */
    private void playHideAnimation() {

        //Plays hide animation
        hideAnimation.play();

        if (onHideCallBack != null)
            onHideCallBack.handle(new ActionEvent());
    }
}