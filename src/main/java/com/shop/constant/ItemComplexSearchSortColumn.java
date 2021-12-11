package com.shop.constant;

public enum ItemComplexSearchSortColumn {
    REG_TIME("regTime"),
    NAME("name"),
    PRICE("price");

    private final String columnName;

    ItemComplexSearchSortColumn(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return columnName;
    }

}
