package com.healthtrack.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple standalone utility to inspect and optionally drop all tables in the configured MySQL schema.
 * WARNING: This is destructive. Only use on a local dev database.
 */
public class MySqlResetTool {
    private static final String URL = "jdbc:mysql://localhost:3306/healthtrackdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "123456";

    public static void main(String[] args) {
        boolean doDrop = args.length > 0 && "--confirm-drop".equals(args[0]);
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS); Statement st = conn.createStatement()) {
            System.out.println("Connected to MySQL. Inspecting tables...");
            ResultSet rs = st.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='healthtrackdb'");
            List<String> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            if (tables.isEmpty()) {
                System.out.println("No tables found. Database already empty.");
                return;
            }
            System.out.println("Found tables (" + tables.size() + "): " + tables);
            if (!doDrop) {
                System.out.println("Run again with --confirm-drop to drop ALL tables in schema 'healthtrackdb'.");
                return;
            }
            st.execute("SET FOREIGN_KEY_CHECKS=0");
            for (String t : tables) {
                String sql = "DROP TABLE IF EXISTS `" + t + "`";
                st.execute(sql);
                System.out.println("Dropped table: " + t);
            }
            st.execute("SET FOREIGN_KEY_CHECKS=1");
            System.out.println("All tables dropped.");
        } catch (Exception e) {
            System.err.println("Failed to connect or operate on database: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
