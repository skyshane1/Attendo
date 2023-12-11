package com.example.attendo;

import java.io.File;
import java.sql.*;

public class DBUtil {
    private static final String connStr = "jdbc:sqlite:." + File.separatorChar + "swipes.sqlite";
    private static Connection connection = null;
    private static PreparedStatement stmt = null;
    private static final String JDBC_DRIVER= "org.sqlite.JDBC";
    public static void dbConnect() throws SQLException, ClassNotFoundException {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Driver dude?");
            e.printStackTrace();
            throw e;
        }

        System.out.println("Oracle JDBC Driver Registered!");
        //Establish the Oracle Connection using Connection String
        try {
            connection = DriverManager.getConnection(connStr);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console" + e);
            e.printStackTrace();
            throw e;
        }
    }

    public static void dbInsert(String name, String banner_id) throws SQLException {
        stmt = connection.prepareStatement("INSERT INTO swipes (name, banner_id, swipes) VALUES (?, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, banner_id);
        stmt.setInt(3,1);
        stmt.executeUpdate();
    }

    public static int dbGetSwipes(String banner_id) throws SQLException{
        stmt = connection.prepareStatement("SELECT swipes from swipes WHERE banner_id=?");
        stmt.setString(1, banner_id);
        try {
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1);
        } catch(Exception e) {
            return 0;
        }
    }

    public static String dbGetName(String banner_id) throws SQLException{
        stmt = connection.prepareStatement("SELECT name from swipes WHERE banner_id=?");
        stmt.setString(1, banner_id);
        try {
            ResultSet rs = stmt.executeQuery();
            String string = rs.getString(1);
            String[] splitt = string.split("/");
            return splitt[1] + " " + splitt[0];
        } catch(Exception e) {
            return "null";
        }
    }

    public static void dbDestroy() throws SQLException {
        stmt = connection.prepareStatement("DELETE FROM swipes");
        try {
            stmt.executeUpdate();
        } catch(Exception e) {
            System.out.println("something bad happened");
        }
    }

    public static ResultSet dbGetAll() throws SQLException{
        stmt = connection.prepareStatement("SELECT * from swipes");
        try {
            return stmt.executeQuery();
        } catch(Exception e) {
            return null;
        }
    }

    public static void dbUpdate(String name, String banner_id) throws SQLException {
        stmt = connection.prepareStatement("UPDATE swipes set swipes=swipes+1, name=? where banner_id=?");
        stmt.setString(1, name);
        stmt.setString(2, banner_id);
        stmt.executeUpdate();
    }
}
