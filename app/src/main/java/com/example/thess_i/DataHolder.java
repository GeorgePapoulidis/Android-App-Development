/**package com.example.thess_i;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {
    public static class Admin {
        public String username;
        public String password;

        public Admin(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class Table {
        public int number;
        public int capacity;
        public boolean isAvailable;

        public Table(int number, int capacity, boolean isAvailable) {
            this.number = number;
            this.capacity = capacity;
            this.isAvailable = isAvailable;
        }
    }

    public static class Shop {
        public String name;
        public List<Table> tables;

        public Shop(String name) {
            this.name = name;
            this.tables = new ArrayList<>();
        }
    }

    public static Admin admin = new Admin("111", "333");
    public static List<Shop> shops = new ArrayList<>();

    static {
        // Initialize the admin's shop with 3 tables
        Shop adminShop = new Shop("Hliotropio");
        adminShop.tables.add(new Table(1, 4, true));
        adminShop.tables.add(new Table(2, 4, true));
        adminShop.tables.add(new Table(3, 4, true));
        adminShop.tables.add(new Table(4, 4, true));
        adminShop.tables.add(new Table(5, 4, true));
        adminShop.tables.add(new Table(6, 4, true));
        adminShop.tables.add(new Table(7, 4, true));
        adminShop.tables.add(new Table(8, 4, true));
        adminShop.tables.add(new Table(9, 4, true));
        adminShop.tables.add(new Table(10, 4, true));
        adminShop.tables.add(new Table(11, 4, true));
        shops.add(adminShop);
    }
}*/
