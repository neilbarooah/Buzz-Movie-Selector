package com.thundercats50.moviereviewer.database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.InputMismatchException;
import java.util.InvalidPropertiesFormatException;

/**
 * TODO: Encrypt passwords
 * @author Scott Heston
 * @version 2.0.0
 * Documentation:
 * https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-connect-drivermanager.html
 */
public abstract class DBConnector  {

    Connection connection;
    Statement statement;


    public DBConnector() throws SQLException {
        connect();
    }

    /**
     * Creates connection to mySQL database on connection.
     * @return Connection
     * @throws ClassNotFoundException if Driver lib dependency not found
     * @throws SQLException if authentication issues with DB
     */
    public void connect() throws SQLException {
        //the implementation should notify the user of these errors if there is no internet access
        //i.e if the database cannot be reached
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // If this line throws an error, make sure gradle is including the java/mySQL connector
            // jar in the class folder. If "bad class file magic", bug with gradle and android:
            // https://github.com/windy1/google-places-api-java/issues/18
            // fix by including lib, not downloading it from Maven
            String url = "jdbc:mysql://sql5.freemysqlhosting.net";
            String user = "sql5107476";
            String pass = "YMVSuA8eWm";
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException drivExc) {
            Log.e("DBError", "The database driver has failed.");
            Log.d("DBC ClassNtFndException",
                    "Could not access database username/password. Check DB Driver.");
        } catch (IllegalAccessException iae) {
            Log.d("ClassNotFoundException", "Could not access database username/password. "
                    + "Check DB Driver.");
        } catch (SQLException sqle) {
            Log.d("Connection Error", "Check internet for MySQL access." + sqle.getMessage() + sqle.getSQLState());
            for (Throwable e : sqle) {
                e.printStackTrace(System.err);
                Log.d("Connection Error", "SQLState: " +
                        ((SQLException) e).getSQLState());

                Log.d("Connection Error", "Error Code: " +
                        ((SQLException) e).getErrorCode());

                Log.d("Connection Error", "Message: " + e.getMessage());

                Throwable t = sqle.getCause();
                while(t != null) {
                    Log.d("Connection Error", "Cause: " + t);
                    t = t.getCause();
                }
            }
            throw sqle;
        }
        catch (Exception e) {
            throw new SQLException("Unknown connection error: assume no internet.", e);
        }
    }


    /**
     * Must be run to disconnect connection when finished with DB.
     */
    public void disconnect() {
        Log.d("DBC Logout", "entered");
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException sqlEx) {
                Log.d("DBC Logout", sqlEx.getMessage() + sqlEx.getErrorCode() + sqlEx.toString());
            }
            // ignore, means connection was already closed
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException sqlEx) {
                Log.d("DBC Logout", sqlEx.getMessage() + sqlEx.getErrorCode() + sqlEx.toString());
            }
            // ignore, means connection was already closed
        }
    }

}
