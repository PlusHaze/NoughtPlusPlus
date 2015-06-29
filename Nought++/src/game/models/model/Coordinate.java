package game.models.model;

public class Coordinate {

    private int column;
    private int row;

    /**
     * The coordinate of the object
     * @param column The column of the coordinate
     * @param row The row of the coordinate
     */
    public Coordinate(int column, int row) {
        this.column = column;
        this.row = row;
    }

    /**
     * Constructs a new coordinate from a previous one
     * @param c The new coordinate to use
     */
    public Coordinate(Coordinate c) {
        this.column = c.column();
        this.row = c.row();
    }

    /**
     * Returns the column value
     * @return the column value
     */
    public int column() {
        return column;
    }

    /**
     * Returns the row value
     * @return the row value
     */
    public int row() {
        return row;
    }
}
