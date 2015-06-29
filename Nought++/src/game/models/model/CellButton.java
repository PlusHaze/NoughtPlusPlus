package game.models.model;

import game.models.logic.Player;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class CellButton extends Button {

    private Coordinate coordinate;
    private Player player;

    /**
     * Constructs the CellButton and associating a coordinate with the button
     * @param coordinate The coordinates to associate the CellButtons with
     */
    public CellButton(Coordinate coordinate) {
        this.coordinate = new Coordinate(coordinate.column(), coordinate.row());
    }

    /**
     * Constructs the CellButton and associating a coordinate with the button
     * @param coordinate The coordinates to associate the CellButtons with
     * @param text The text to display on the button
     */
    public CellButton(Coordinate coordinate, String text) {
        this.coordinate = new Coordinate(coordinate.column(), coordinate.row());

        super.setText(text);
    }

    /**
     *
     * @return the coordinate associated with the button
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     *
     * @return the Player is associated with the button.
     * e.g. the player that clicked the button
     */
    public Player getAttachedPlayer() {
        return this.player;
    }

    /**
     * Associates a player with a button
     * @param player The player to associate the button with
     */
    public  void setAttachedPlayer(Player player){
        this.player = player;
        this.setGraphic(null);

        //Sets the players image to the button
        if (player != null && player.getImage() != null) {

            ImageView imgView  = new ImageView(player.getImage());

            //Resizes the image if it's too large to fit the button

            if (imgView.getImage().getHeight() > this.getHeight())
                imgView.setFitHeight(this.getHeight() - 20);

            if (imgView.getImage().getWidth() > this.getWidth())
                imgView.setFitWidth(this.getWidth() - 20);

            imgView.setPreserveRatio(true);

            //Setting the players image to the button
            this.setGraphic(imgView);
        }
    }
}