package game.models.logic;

import javafx.scene.image.Image;

public class Player {

    private String name;
    private Image Image;
    private int score;

    /**
     *
     * @param name The players name
     * @param img The players image
     */
    public Player(String name, Image img) {
        this.name = name;
        this.Image = img;
        this.score = 0;
    }

    /**
     *
     * @param p Constricting a new player from a previous player
     */
    public Player(Player p) {
        this.name = p.getName();
        this.Image = p.getImage();
        this.score = 0;
    }

    /**
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the players image
     */
    public Image getImage() {
        return this.Image;
    }

    /**
     *
     * @return the players score
     */
    public int getScore() {
        return score;
    }

    /**
     *
     * @param score Returns the players score
     */
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Player) {
            Player p  = (Player) obj;

            boolean nameEqual = this.getName().equals(p.getName());
            boolean scoreEqual = this.getScore() == p.getScore();
            boolean imageEqual = this.getImage().equals(p.getImage());

            return nameEqual && scoreEqual && imageEqual;
        }

        return this == obj;
    }
}
