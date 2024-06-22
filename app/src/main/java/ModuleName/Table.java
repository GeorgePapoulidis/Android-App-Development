package ModuleName;

import java.math.BigInteger;

/**
 * The Table class represents a table with an id, name, store id, location (x, y), capacity, and state.
 */
public class Table {

    // Fields for storing table information
    private BigInteger id;
    private String name;
    private BigInteger store;
    private Integer x;
    private Integer y;
    private Integer capacity;
    private String state;

    /**
     * Constructor for creating a Table object with specified values.
     *
     * @param id       the id of the table
     * @param name     the name of the table
     * @param store    the store id the table belongs to
     * @param x        the x-coordinate of the table
     * @param y        the y-coordinate of the table
     * @param capacity the capacity of the table
     * @param state    the state of the table
     */
    public Table(BigInteger id, String name, BigInteger store, Integer x, Integer y, Integer capacity, String state) {
        this.id = id;
        this.name = name;
        this.store = store;
        this.x = x;
        this.y = y;
        this.capacity = capacity;
        this.state = state;
    }

    /**
     * Copy constructor for creating a Table object from another Table object.
     *
     * @param t the Table object to copy
     */
    public Table(Table t) {
        this.id = t.id;
        this.name = t.name;
        this.store = t.store;
        this.x = t.x;
        this.y = t.y;
        this.capacity = t.capacity;
        this.state = t.state;
    }

    /**
     * Gets the id of the table.
     *
     * @return the id of the table
     */
    public BigInteger getId() {
        return this.id;
    }

    /**
     * Sets the id of the table.
     *
     * @param id the new id of the table
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Gets the name of the table.
     *
     * @return the name of the table
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the table.
     *
     * @param name the new name of the table
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the x-coordinate of the table.
     *
     * @return the x-coordinate of the table
     */
    public Integer getX() {
        return this.x;
    }

    /**
     * Sets the x-coordinate of the table.
     *
     * @param x the new x-coordinate of the table
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the table.
     *
     * @return the y-coordinate of the table
     */
    public Integer getY() {
        return this.y;
    }

    /**
     * Sets the y-coordinate of the table.
     *
     * @param y the new y-coordinate of the table
     */
    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * Gets the capacity of the table.
     *
     * @return the capacity of the table
     */
    public Integer getCapacity() {
        return this.capacity;
    }

    /**
     * Sets the capacity of the table.
     *
     * @param capacity the new capacity of the table
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the state of the table.
     *
     * @return the state of the table
     */
    public String getState() {
        return this.state;
    }

    /**
     * Sets the state of the table.
     *
     * @param state the new state of the table
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the store id the table belongs to.
     *
     * @return the store id the table belongs to
     */
    public BigInteger getStore() {
        return this.store;
    }

    /**
     * Prints the details of the table.
     */
    public void printTableData() {
        System.out.println("ID: " + getId() + " " +
                "Name: " + getName() + " " +
                "X: " + getX() + " " +
                "Y: " + getY() + " " +
                "Capacity: " + getCapacity() + " " +
                "State: " + getState() + " " +
                "Store ID: " + getStore());
    }
}
