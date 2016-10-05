package com.thundercats50.moviereviewer.database;

import android.util.Log;

import com.thundercats50.moviereviewer.models.User;
import com.thundercats50.moviereviewer.models.ValidMajors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.InputMismatchException;

/**
 * Created by scottheston on 28/02/16.
 */
public class BlackBoardConnector extends DBConnector {

    public BlackBoardConnector() throws SQLException {
        super();
    }

    /**
     * Method to add user to database. Screens info to prevent duplicates.
     * @param Email
     * @param password
     * @return boolean true if succesfully created
     * @throws SQLException
     */
    public boolean setNewUser(String Email, String password) throws SQLException,
            InputMismatchException {
        ResultSet resultSet = null;
        try {
            if (!isEmailValid(Email)) {
                throw new InputMismatchException("Incorrectly formatted email.");
                //DO NOT CHANGE MESSAGE: USED IN REGISTER-ACTIVITY LOGIC
            }
            if (checkIfUser(Email)) {
                throw new InputMismatchException("User email is already registered.");
                //DO NOT CHANGE MESSAGE: USED IN REGISTER-ACTIVITY LOGIC
            }
            if (!isPasswordValid(password)) {
                throw new InputMismatchException("Password must be alphanumeric and longer than six"
                        + "characters.");
                //DO NOT CHANGE MESSAGE: USED IN REGISTER-ACTIVITY LOGIC
            }
            statement = connection.createStatement();
            String request = "INSERT INTO sql5107476.UserInfo (Email, Password) VALUES ('"
                    + Email + "','" + password + "')";

            int didSucceed = statement.executeUpdate(request);

        }
        catch (ClassNotFoundException cnfe) {
            Log.d("DB Driver Error", cnfe.getMessage());
            return false;
        }
        catch (SQLException e) {
            Log.d("DB Write error", e.getMessage());
            throw e;
        }
        return true;
    }

    /**
     * Uses RegEx to verify emails
     * @param email to check
     * @return ifValid
     */
    private boolean isEmailValid(String email) {
        return (email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")
                && email.length() >= 6);
    }

    /**
     * Uses RegEx to verify pass; must be more than 6 chars and alphnumeric
     * @param password
     * @return ifValid
     */
    private boolean isPasswordValid(String password) {
        return (password.matches("[a-zA-Z0-9]{6,30}") && password.length() > 6);
    }


    /**
     * Method to be run on incorrect login. Updates the Email's incorrect login number on the DB.
     * @param user Email to be check
     */
    public boolean changePass(String user, String pass, String oldPass)
            throws NullPointerException {
        ResultSet resultSet = null;
        try {
            if (!isEmailValid(user) || !isPasswordValid(pass) || !isPasswordValid(oldPass)) {
                throw new InputMismatchException("The given user or pass is formatted incorrectly.");
            }
            if (!verifyUser(user, pass).equals(UserStatus.VERIFIED)) {
                throw new NullPointerException("The given password does "
                        + "not correspond with the DB.");
            }
            statement = connection.createStatement();
            String request = "UPDATE sql5107476.UserInfo SET Password ='"
                    + pass + "' WHERE Email = '" + user + "'";
            PreparedStatement aStatement = connection.prepareStatement(request);
            aStatement.executeUpdate();
            int didSucceed = statement.executeUpdate(request);

            return true;
        } catch (Exception e) {
            Log.d("DB Write error", e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether user already exists. For use
     * when creating a new user, to make sure a user is not
     * overwritten. To verify login, use verifyUser method
     * @param user to check
     * @return isValid
     * @throws SQLException
     */
    public boolean checkIfUser(String user)
            throws ClassNotFoundException, SQLException {
        ResultSet resultSet = getUserPass(user);
        if (resultSet.next()) {
            return true;
        }
        return false;
    }




    /**
     * Verifies user/pass combinations for login
     * @param user to check
     * @param pass to check
     * @return boolean true if valid
     * @throws SQLException
     */
    public UserStatus verifyUser(String user, String pass)
            throws SQLException {
        ResultSet resultSet = getUserPass(user);
        if (resultSet.next()) {
            if (resultSet.getInt("Banned") == 1) {
                return UserStatus.BANNED;
            } else if (resultSet.getInt("LoginAttempts") >= 3) {
                return UserStatus.LOCKED;
            } else if (resultSet.getString("Password").equals(pass)) {
                //there is only 1 entry because there is only
                // one Email selected from DB at a time
                return UserStatus.VERIFIED;
            }
            resultSet.close();
        }
        incrementLoginAttempts(user);
        return UserStatus.BAD_USER;
    }

    /**
     * method to query DB for user information matching Email
     * @param email
     * @return ResultSet
     * @throws SQLException
     */
    public ResultSet getUserPass(String email)
            throws SQLException {
        ResultSet resultSet = null;
        try {
            if (connection == null) connect();
            statement = connection.createStatement();
            //keep making new statements as security method to keep buggy code from accessing
            // old data
            String request = "SELECT Password, Banned, LoginAttempts FROM " +
                    "sql5107476.UserInfo WHERE Email=" + "'" + email +"'";
            resultSet = statement.executeQuery(request);
        } catch (SQLException sqle) {
            Log.e("Database SQLException", sqle.getMessage());
            Log.e("Database SQLState", sqle.getSQLState());
            Log.e("Database VendorError", Integer.toString(sqle.getErrorCode()));
            throw sqle;
        }
        return resultSet;
    }

    /**
     * method to query DB for user information matching Email
     * @return ResultSet
     * @throws SQLException
     */
    public HashSet<User> getAllUsers()
            throws SQLException {
        ResultSet resultSet = null;
        HashSet<User> retVal = new HashSet<>();
        try {
            if (connection == null) connect();
            statement = connection.createStatement();
            //keep making new statements as security method to keep buggy code from accessing
            // old data
            String request = "SELECT FirstName, LastName, Email, LoginAttempts, Banned" +
                    " FROM sql5107476.UserInfo";
            resultSet = statement.executeQuery(request);
            while(resultSet.next()) {
                User currentUser = new User();
                currentUser.setFirstname(resultSet.getString("FirstName"));
                currentUser.setEmail(resultSet.getString("Email"));
                retVal.add(currentUser);
            }
        } catch (SQLException sqle) {
            Log.e("Database SQLException", sqle.getMessage());
            Log.e("Database SQLState", sqle.getSQLState());
            Log.e("Database VendorError", Integer.toString(sqle.getErrorCode()));
            throw sqle;
        }
        return retVal;
    }



    /**
     * method to query DB for user information matching Email
     * @param email
     * @return ResultSet
     * @throws SQLException
     */
    public ResultSet getUserData(String email)
            throws SQLException {
        ResultSet resultSet = null;
        try {
            if (connection == null) connect();
            statement = connection.createStatement();
            //keep making new statements as security method to keep buggy code from accessing
            // old data
            String request = "SELECT FirstName, LastName, Major, Gender, LoginAttempts, Banned" +
                    " FROM sql5107476.UserInfo WHERE Email='" + email +"'";
            resultSet = statement.executeQuery(request);
        } catch (SQLException sqle) {
            Log.e("Database SQLException", sqle.getMessage());
            Log.e("Database SQLState", sqle.getSQLState());
            Log.e("Database VendorError", Integer.toString(sqle.getErrorCode()));
            throw sqle;
        }
        return resultSet;
    }


    /**
     * method to return all users of given major
     * @param major to check for
     * @return ResultSet of users
     * @throws SQLException on connection error
     */
    public ResultSet getUsersWithMajor(String major)
            throws SQLException {
        ResultSet resultSet = null;
        try {
            if (connection == null) connect();
            statement = connection.createStatement();
            //keep making new statements as security method to keep buggy code from accessing
            // old data
            String request = "SELECT Email FROM sql5107476.UserInfo WHERE Major="
                    + "'" + major +"'";
            resultSet = statement.executeQuery(request);
        } catch (SQLException sqle) {
            Log.e("Database SQLException", sqle.getMessage());
            Log.e("Database SQLState", sqle.getSQLState());
            Log.e("Database VendorError", Integer.toString(sqle.getErrorCode()));
            throw sqle;
        }
        return resultSet;
    }


    /**
     * Method to update User's data matching email
     * @return boolean true if success
     */
    public boolean setUserData(String firstName, String lastName, String major, String gender,
                               String email) {
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            String request = "UPDATE sql5107476.UserInfo SET " +
                    "FirstName = '" + firstName + "',"
                    + "LastName = '" + lastName + "',"
                    + "Major = '" + major + "',"
                    + "Gender = '" + gender + "' WHERE email ='" + email + "'";
            PreparedStatement aStatement = connection.prepareStatement(request);
            aStatement.executeUpdate();
            statement.executeUpdate(request);
            return true;
        } catch (Exception e) {
            Log.d("DB Write error", e.getMessage());
            return false;
        }
    }


    /**
     * Method to be run on incorrect login. Updates the Email's incorrect login number on the DB.
     * @param user Email to be incremented
     */
    public boolean incrementLoginAttempts(String user) {
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            int newVal = 1 + getLoginAttempts(user);
            String request = "UPDATE sql5107476.UserInfo SET LoginAttempts ="
                    + newVal + " WHERE Email = '" + user + "'";
            int didSucceed = statement.executeUpdate(request);
            return true;
        } catch (Exception e) {
            Log.d("DB Write error", e.getMessage());
            return false;
        }
    }


    /**
     * Method to be run on incorrect login. Updates the Email's incorrect login number on the DB.
     * @param user Email to be incremented
     */
    public boolean resetLoginAttempts(String user) {
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            String request = "UPDATE sql5107476.UserInfo SET LoginAttempts ="
                    + 0 + " WHERE Email = '" + user + "'";
            int didSucceed = statement.executeUpdate(request);
            return true;
        } catch (Exception e) {
            Log.d("DB Write error", e.getMessage());
            return false;
        }
    }


    /**
     * Method to lock or unlock user.
     * @param user  to update
     * @param isBanned value to update
     */
    public boolean setBanned(String user, boolean isBanned) {
        ResultSet resultSet = null;
        int isBannedInt = (isBanned ? 1 : 0);
        try {
            statement = connection.createStatement();
            String request = "UPDATE sql5107476.UserInfo SET Banned ="
                    + isBannedInt + " WHERE Email = '" + user + "'";
            int didSucceed = statement.executeUpdate(request);
            return true;
        } catch (Exception e) {
            Log.d("DB Write error", e.getMessage());
            return false;
        }
    }


    /**
     * Returns 1000 if user not found.
     * @param user
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public int getLoginAttempts(String user) throws ClassNotFoundException, SQLException {
        int retVal = 1000;
        ResultSet resultSet = null;
        resultSet = getUserData(user);
        if (resultSet.next()) {
            retVal = resultSet.getInt("LoginAttempts");
            //there is only 1 entry because there is only one Email selected from DB at
            // a time; 3 because resultSet contains user, pass, attempts
        }
        resultSet.close();
        return retVal;
    }

    public enum UserStatus {
        VERIFIED, BANNED, LOCKED, BAD_USER, INTERRUPTED_BY_INTERNET
    }

}
