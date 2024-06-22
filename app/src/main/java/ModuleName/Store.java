package ModuleName;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * The Store class represents a store with an id, name, owner, location (x, y), and a list of tables.
 */
public class Store {

    // Fields for storing store information
    private BigInteger id;
    private String name;
    private BigInteger owner;
    private Integer x;
    private Integer y;
    private ArrayList<Table> tables;

    /**
     * Default constructor for creating an empty Store object.
     */
    public Store() {
        this.id = new BigInteger(String.valueOf(0));
        this.name = null;
        this.x = 0;
        this.y = 0;
        this.tables = new ArrayList<>();
    }

    /**
     * Constructor for creating a Store object with specified values.
     *
     * @param id     the id of the store
     * @param name   the name of the store
     * @param owner  the owner id of the store
     * @param x      the x-coordinate of the store
     * @param y      the y-coordinate of the store
     * @param tables the list of tables in the store
     */
    public Store(BigInteger id, String name, BigInteger owner, Integer x, Integer y, ArrayList<Table> tables) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.tables = tables;
    }

    /**
     * Copy constructor for creating a Store object from another Store object.
     *
     * @param s the Store object to copy
     */
    public Store(Store s) {
        this.id = s.id;
        this.name = s.name;
        this.owner = s.owner;
        this.x = s.x;
        this.y = s.y;
        this.tables = s.tables;
    }

    /**
     * Gets the id of the store.
     *
     * @return the id of the store
     */
    public BigInteger getId() {
        return this.id;
    }

    /**
     * Sets the id of the store.
     *
     * @param id the new id of the store
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Gets the name of the store.
     *
     * @return the name of the store
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the store.
     *
     * @param name the new name of the store
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the x-coordinate of the store.
     *
     * @return the x-coordinate of the store
     */
    public Integer getX() {
        return this.x;
    }

    /**
     * Sets the x-coordinate of the store.
     *
     * @param x the new x-coordinate of the store
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the store.
     *
     * @return the y-coordinate of the store
     */
    public Integer getY() {
        return this.y;
    }

    /**
     * Sets the y-coordinate of the store.
     *
     * @param y the new y-coordinate of the store
     */
    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * Gets the list of tables in the store.
     *
     * @return the list of tables in the store
     */
    public ArrayList<Table> getTables() {
        return this.tables;
    }

    /**
     * Sets the list of tables in the store.
     *
     * @param tables the new list of tables in the store
     */
    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    /**
     * Gets the owner id of the store.
     *
     * @return the owner id of the store
     */
    public BigInteger getOwner() {
        return this.owner;
    }

    /**
     * Sets the owner id of the store.
     *
     * @param owner the new owner id of the store
     */
    public void setOwner(BigInteger owner) {
        this.owner = owner;
    }

    /**
     * Adds a table to the store.
     *
     * @param id       the id of the table
     * @param name     the name of the table
     * @param store    the store id the table belongs to
     * @param x        the x-coordinate of the table
     * @param y        the y-coordinate of the table
     * @param capacity the capacity of the table
     * @param aState   the state of the table
     */
    public void addTable(BigInteger id, String name, BigInteger store, Integer x, Integer y, Integer capacity, String aState) {
        Table newTable = new Table(id, name, store, x, y, capacity, aState);
        this.tables.add(newTable);
    }

    /**
     * Deletes a table from the store.
     *
     * @param aTable the table to delete
     * @return true if the table was deleted, false otherwise
     */
    public boolean deleteTable(Table aTable) {
        return this.tables.remove(aTable);
    }

    /**
     * Gets a table by its id.
     *
     * @param id the id of the table
     * @return the table with the specified id, or null if not found
     */
    public Table getTableById(BigInteger id) {
        for (Table zing : this.tables) {
            if (zing.getId().equals(id))
                return zing;
        }
        return null;
    }

    /**
     * Gets a table by its name.
     *
     * @param name the name of the table
     * @return the table with the specified name, or null if not found
     */
    public Table getTableByName(String name) {
        for (Table zing : this.tables) {
            if (zing.getName().equals(name))
                return zing;
        }
        return null;
    }

    /**
     * Prints the details of the store and its tables.
     */
    public void printStore() {
        System.out.println("ID: " + getId() + " " +
                "Name: " + getName());
        System.out.println("X: " + getX() + " " +
                "Y: " + getY());
        System.out.println("Tables:");
        if (this.tables != null) {
            for (Table zing : this.tables) {
                zing.printTableData();
            }
        }
    }
}
