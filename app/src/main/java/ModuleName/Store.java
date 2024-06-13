package ModuleName;

import java.math.BigInteger;
import java.util.ArrayList;

public class Store {

    private BigInteger id;
    private String name;
    private BigInteger owner;
    private Integer x;
    private Integer y;
    private ArrayList<Table> tables;

    public Store(){
        this.id= new BigInteger(String.valueOf(0));
        this.name=null;
        this.x=0;
        this.y=0;
        this.tables=new ArrayList<>();
    }



    public Store(BigInteger id, String name, BigInteger owner, Integer x, Integer y, ArrayList<Table> tables){
        this.id=id;
        this.name=name;
        this.owner=owner;
        this.x=x;
        this.y=y;
        this.tables=tables;
    }
    public Store(Store s){
        this.id=s.id;
        this.name=s.name;
        this.owner=s.owner;
        this.x=s.x;
        this.y=s.y;
        this.tables=s.tables;
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

    public ArrayList<Table> getTables() {
        return this.tables;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    public BigInteger getOwner() {
        return this.owner;
    }

    public void setOwner(BigInteger owner) {
        this.owner = owner;
    }

    public void addTable(BigInteger id, String name,BigInteger store,Integer x, Integer y, Integer capacity, String aState){
        Table newTable=new Table(id,name,store,x,y,capacity,aState);
        this.tables.add(newTable);
    }

    public boolean deleteTable(Table aTable){
        return this.tables.remove(aTable);
    }

    public Table getTableById(BigInteger id){
        for(Table zing:this.tables){
            if(zing.getId().equals(id))
                return zing;
        }
        return null;
    }

    public Table getTableByName(String name){
        for(Table zing:this.tables){
            if(zing.getName().equals(name))
                return zing;
        }
        return null;
    }

    public void printStore(){
        System.out.println("ID:"+ getId() + " " +
                "Name:" + getName());
        System.out.println("X:" + getX() + " " +
                "Y:" + getY());
        System.out.println("Tables:");
        if (this.tables!=null){
            for(Table zing:this.tables){
                zing.printTableData();
            }
        }
    }
}

