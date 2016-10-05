package com.thundercats50.moviereviewer.database;

import android.util.Log;

import com.thundercats50.moviereviewer.models.User;

import org.junit.Test;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.InputMismatchException;

/**
 * Created by neilbarooah on 11/04/16.
 */
public class BlackBoardConnectorTest {

    // private static User aUser;
    User aUser = new User("hello@gmail.com", "Neil", "Barooah","CS", "M");

    /**
     * run to set up a user
     * @throws Exception
     */
    // @Before
    //public void setup() throws Exception {
    //    aUser = new User("hello@gmail.com", "Neil", "Barooah","CS", "M");
    //}

    /**
     * test the user
     */
    @Test
    public void testUser() {
        Assert.assertEquals("Wrong email ID", "hello@gmail.com", aUser.getEmail());
        Assert.assertEquals("Wrong first name", "Neil", aUser.getFirstname());
        Assert.assertEquals("Wrong last name", "Barooah", aUser.getLastname());
        Assert.assertEquals("Wrong major", "CS", aUser.getMajor());
        Assert.assertEquals("Wrong gender", "M", aUser.getGender());
    }

    /**
     * check for incorrect email format
     */
    @Test(expected = InputMismatchException.class)
    public void testIncorrectEmail() {
        try {
            BlackBoardConnector bbc = new BlackBoardConnector();
            boolean result = bbc.setNewUser("hello", "password");
        } catch (SQLException sqlException) {
            Log.d("SQL Exception", "Connect to internet");
        }
    }

    /**
     * check if user already exists
     */
    @Test(expected = InputMismatchException.class)
    public void userExists() {
        try {
            BlackBoardConnector bbc = new BlackBoardConnector();
            boolean result = bbc.setNewUser("user@test.ing", "password");
        } catch (SQLException sqlException) {
            Log.d("SQL Exception", "Connect to internet");
        }
    }

    /**
     * check is password is valid
     */
    @Test(expected = InputMismatchException.class)
    public void passwordShort() {
        try {
            BlackBoardConnector bbc = new BlackBoardConnector();
            boolean result = bbc.setNewUser("yaya@gmail.com", "ay");
        } catch (SQLException sqlException) {
            Log.d("SQL Exception", "Connect to internet");
        }
    }

    /**
     * register a user
     */
    @Test
    public void registerUser() {
        try {
            BlackBoardConnector bbc = new BlackBoardConnector();
            boolean result = bbc.setNewUser("yayayya@gmail.com", "jejejejeje");
            Assert.assertTrue("User should be registered successfully", result);
        } catch (SQLException sqlException) {
            Log.d("SQL Exception", "Connect to internet");
        }
    }

    /**
     * test check if user
     */
    @Test
    public void testCheckUser() {
        try {
            BlackBoardConnector bbc = new BlackBoardConnector();
            boolean result = bbc.checkIfUser("user@test.ing");
            Assert.assertTrue("User should be registered successfully", result);
        } catch (SQLException sqlException) {
            Log.d("SQL Exception", "Connect to internet");
        } catch (ClassNotFoundException except) {
            Log.d("Class not found", "error");
        }
    }

    /**
     * test check if user
     */
    @Test
    public void userNotExist() {
        try {
            BlackBoardConnector bbc = new BlackBoardConnector();
            boolean result = bbc.checkIfUser("nhahahahaadskad@gmail.com");
            Assert.assertFalse("this user doesn't exist", result);
        } catch (SQLException sqlException) {
            Log.d("SQL Exception", "Connect to internet");
        } catch (ClassNotFoundException except) {
            Log.d("Class not found", "error");
        }
    }
}