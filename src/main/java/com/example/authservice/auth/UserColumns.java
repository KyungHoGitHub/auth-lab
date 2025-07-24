package com.example.authservice.auth;

public enum UserColumns {
    USERID("userId"),
    USER_NAME("userName");

    private String columnName;

    UserColumns(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
