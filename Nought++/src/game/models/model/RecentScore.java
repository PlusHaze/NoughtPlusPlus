package game.models.model;

import game.models.logic.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecentScore {

    private Player player1;
    private Player player2;
    private String date;
    private int gridDimension;

    /**
     * A recent score object
     * @param p1 The player the recent score record is to be about
     * @param p2 The second player the recent score record is to be about
     * @param dimension The dimension of the grid that was played on
     */
    public RecentScore(Player p1, Player p2, int dimension) {
        this.player1 = p1;
        this.player2 = p2;
        this.gridDimension = dimension;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.date = dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * A recent score object
     * @param p1 The player the recent score record is to be about
     * @param p2 The second player the recent score record is to be about
     * @param dimension The dimension of the grid that was played on
     * @param date The date the recent score was created
     */
    public RecentScore(Player p1, Player p2, int dimension, String date) {
        this.player1 = p1;
        this.player2 = p2;
        this.date = date;
        this.gridDimension = dimension;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", player1.getName(), player2.getName());
    }

    public String getNames() {
        return toString();
    }

    public String getScores() {
        return String.format("%d - %d", player1.getScore(), player2.getScore());
    }

    public String getDate() {
        return date;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    /**
     *
     * @return A formatted string representation of the grid dimension
     */
    public String getFormatDimension() {
        return String.format("%dx%d", gridDimension, gridDimension);
    }

    /**
     *
     * @return An integer representation of the grid dimension
     */
    public int getAbsDimension() {
        return this.gridDimension;
    }
}