package com.thundercats50.moviereviewer;

import junit.framework.TestCase;

/**
 * Created by scottheston on 07/02/16.
 */
public class DBConnectorTest extends TestCase {

    DBConnector dbc;

    public void setUp() throws Exception {
        dbc = new DBConnector();
    }

    public void tearDown() throws Exception {
        dbc.disconnect();
    }


    public void testCheckIfUser() throws Exception {
        assertTrue("Make sure user is in DB.", dbc.checkIfUser("Scooter"));
        assertFalse("Check against false positives.", dbc.checkIfUser("ranStringadsffdsavd"));
    }

    public void testVerifyUser() throws Exception {
        assertTrue("Make sure user/pass is in DB.", dbc.verifyUser("Scooter", "testpassnoauth"));
    }


    public void testIncrementLoginAttempts() throws Exception {
        int userAttempts = dbc.getLoginAttempts("Scooter");
        System.out.println("Manually check against data in server:");
        System.out.println(userAttempts);
        //also check get user attemps
        dbc.incrementLoginAttempts("Scooter");
        assertEquals("Make sure attemps has been incremented.", userAttempts + 1,
                dbc.getLoginAttempts("Scooter"));

    }
}