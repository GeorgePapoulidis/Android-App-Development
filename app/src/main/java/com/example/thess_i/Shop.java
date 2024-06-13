package com.example.thess_i;

import java.util.Arrays;
import java.util.List;

public class Shop {
    private String name;
    private List<TableData> tables;

    public Shop(String name, TableData[] tables) {
        this.name = name;
        this.tables = Arrays.asList(tables);
    }

    public String getName() {
        return name;
    }

    public List<TableData> getTables() {
        return tables;
    }
}
