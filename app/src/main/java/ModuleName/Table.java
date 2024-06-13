package ModuleName;

import java.math.BigInteger;


public class Table {
    private BigInteger id;
    private String name;
    private BigInteger store;
    private Integer x;
    private Integer y;
    private Integer capacity;
    private String state;

    public Table(BigInteger id, String name, BigInteger store, Integer x, Integer y, Integer capacity, String state){
        this.id=id;
        this.name=name;
        this.store=store;
        this.x=x;
        this.y=y;
        this.capacity=capacity;
        this.state=state;
    }
    public Table(Table t){
        this.id=t.id;
        this.name=t.name;
        this.store=t.store;
        this.x=t.x;
        this.y=t.y;
        this.capacity=t.capacity;
        this.state=t.state;
    }

    public BigInteger getId() {
        return this.id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getX() {
        return this.x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return this.y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public BigInteger getStore(){
        return this.store;
    }

    public void printTableData(){
        System.out.println("ID:" + getId()+ " " +
                "Name:" + getName()+ " " +
                "X:" + getX()+ " " +
                "Y:" + getY()+ " " +
            "Capacity:" + getCapacity()+ " " +
                "State:" + getState() + " " +
            "Store ID:" + getStore());
    }

}

