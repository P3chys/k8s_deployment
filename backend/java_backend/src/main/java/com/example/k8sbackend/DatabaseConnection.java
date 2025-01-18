package com.example.k8sbackend;

// Using JDBC
import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    // Hardcoded database connection properties
    private static final String URL = "jdbc:postgresql://host.docker.internal:5432/mydb";
    private static final String USER = "admin";
    private static final String PASSWORD = "adminpassword";

    public static void extractSchemaJDBC() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Get all tables
            ResultSet tables = metaData.getTables(null, "public", "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\nTable: " + tableName);
                
                // Get columns for each table
                ResultSet columns = metaData.getColumns(null, "public", tableName, "%");
                System.out.println("Columns:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("TYPE_NAME");
                    int size = columns.getInt("COLUMN_SIZE");
                    boolean nullable = columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                    
                    System.out.printf("- %s (%s", columnName, dataType);
                    if (size > 0) System.out.printf("(%d)", size);
                    System.out.printf(", %s)\n", nullable ? "nullable" : "not null");
                }
                
                // Get primary keys
                ResultSet primaryKeys = metaData.getPrimaryKeys(null, "public", tableName);
                System.out.println("Primary Keys:");
                while (primaryKeys.next()) {
                    String pkName = primaryKeys.getString("COLUMN_NAME");
                    System.out.println("- " + pkName);
                }
                
                // Get foreign keys
                ResultSet foreignKeys = metaData.getImportedKeys(null, "public", tableName);
                System.out.println("Foreign Keys:");
                while (foreignKeys.next()) {
                    String fkName = foreignKeys.getString("FKCOLUMN_NAME");
                    String pkTable = foreignKeys.getString("PKTABLE_NAME");
                    String pkColumn = foreignKeys.getString("PKCOLUMN_NAME");
                    System.out.printf("- %s -> %s.%s\n", fkName, pkTable, pkColumn);
                }
                
                // Get indexes
                ResultSet indexes = metaData.getIndexInfo(null, "public", tableName, false, false);
                System.out.println("Indexes:");
                while (indexes.next()) {
                    String indexName = indexes.getString("INDEX_NAME");
                    if (indexName != null) { // Skip null indexes (usually primary keys)
                        String columnName = indexes.getString("COLUMN_NAME");
                        boolean unique = !indexes.getBoolean("NON_UNIQUE");
                        System.out.printf("- %s on %s (unique: %s)\n", indexName, columnName, unique);
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}