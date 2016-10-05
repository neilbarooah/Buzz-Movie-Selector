package com.thundercats50.moviereviewer.database;

import android.util.Log;

import com.thundercats50.moviereviewer.models.Rating;
import com.thundercats50.moviereviewer.models.SingleMovie;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.InputMismatchException;

/**
 * Created by scottheston on 28/02/16.
 */
public class RepositoryConnector extends DBConnector {

    public RepositoryConnector() throws ClassNotFoundException, SQLException {
        super();
    }

    /**
     * method to query DB for ratings matching username
     * @param email to search for ratings
     * @return ResultSet (which can be accessed through a for-while loop)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public ResultSet getUserRatings(String email)
            throws ClassNotFoundException, SQLException {
        ResultSet resultSet = null;
        try {
            if (connection == null) connect();
            statement = connection.createStatement();
            //keep making new statements as security method to keep buggy code from accessing
            // old data
            String request = "SELECT MovieID,NumericalRating," +
                    "TextReview, PhotoURL FROM sql5107476.RatingInfo WHERE Email="
                    + "'" + email +"' ORDER BY NumericalRating";
            resultSet = statement.executeQuery(request);
        } catch (SQLException sqle) {
            Log.e("Database SQLException", sqle.getMessage());
            Log.e("Database SQLState", sqle.getSQLState());
            Log.e("Database VendorError", Integer.toString(sqle.getErrorCode()));
        }
        return resultSet;
    }


    /**
     * method to query DB for ratings matching major
     * @param major to search for ratings
     * @return ResultSet (which can be accessed through a for-while loop)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public HashSet<SingleMovie> getAllByMajor(String major)
            throws ClassNotFoundException, SQLException {
        HashSet<SingleMovie> retVal = new HashSet<>();
        try {
            if (connection == null) connect();
            statement = connection.createStatement();
            //keep making new statements as security method

            BlackBoardConnector bbc = new BlackBoardConnector();
            ResultSet users = bbc.getUsersWithMajor(major);
            ResultSet current = null;
            while (users.next()) {
                String request = "SELECT MovieID, MovieName, NumericalRating," +
                        "TextReview, PhotoURL, Email, Synopsis FROM sql5107476.RatingInfo WHERE Email="
                        + "'" + users.getString("Email") +"' ORDER BY NumericalRating";
                Log.d("DB_QUERY", request);
                current = statement.executeQuery(request);
                SingleMovie currentMovie = new SingleMovie();
                Rating currentRating = new Rating();
                while (current.next() && !(current.isAfterLast())) {
                    currentMovie.setId((long) current.getDouble("MovieID"));
                    currentRating.setUser(current.getString("Email"));
                    currentRating.setNumericalRating(current.getInt("NumericalRating"));
                    currentRating.setTextReview(current.getString("TextReview"));
                    if (retVal.contains(currentMovie)) {
                        for (SingleMovie m : retVal) {
                            if (m.equals(currentMovie)) {
                                m.addUserRating(current.getString("Email"), currentRating);
                            }
                        }
                    } else {
                        currentMovie.addUserRating(current.getString("Email"), currentRating);
                        currentMovie.setTitle(current.getString("MovieName"));
                        currentMovie.setThumbnailURL(current.getString("PhotoURL"));
                        currentMovie.setSynopsis(current.getString("Synopsis"));
                        retVal.add(currentMovie);
                    }
                }
            }

        } catch (SQLException sqle) {
            Log.e("Database SQLException", sqle.getMessage());
            Log.e("Database SQLState", sqle.getSQLState());
            Log.e("Database VendorError", Integer.toString(sqle.getErrorCode()));
        }
        return retVal;
    }


    /**
     * method to query DB for rating information matching movie's name
     * @param movieID to search for ratings
     * @return ResultSet (which can be accessed through a for-while loop)
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public HashSet<SingleMovie> getMovieRatings(long movieID)
            throws ClassNotFoundException, SQLException {
        HashSet<SingleMovie> retVal = new HashSet<>();
        try {
            if (connection == null) connect();
            statement = connection.createStatement();
            //keep making new statements as security method to keep buggy code from accessing
            // old data
            String request = "SELECT MovieID, MovieName, NumericalRating,"+
                    "TextReview, PhotoURL, Email, Synopsis FROM sql5107476.RatingInfo WHERE MovieID="
                    + "" + movieID +"";
            ResultSet current = statement.executeQuery(request);
            SingleMovie currentMovie = new SingleMovie();
            Rating currentRating = new Rating();
            while (current.next() && !(current.isAfterLast())) {
                currentMovie.setId((long) current.getDouble("MovieID"));
                currentMovie.setTitle(current.getString("MovieName"));
                currentMovie.setThumbnailURL(current.getString("PhotoURL"));
                currentMovie.setSynopsis(current.getString("Synopsis"));
                currentRating.setUser(current.getString("Email"));
                currentRating.setNumericalRating(current.getInt("NumericalRating"));
                currentRating.setTextReview(current.getString("TextReview"));
                currentMovie.addUserRating(current.getString("Email"), currentRating);
                retVal.add(currentMovie);
            }
        } catch (SQLException sqle) {
            Log.e("Database SQLException", sqle.getMessage());
            Log.e("Database SQLState", sqle.getSQLState());
            Log.e("Database VendorError", Integer.toString(sqle.getErrorCode()));
        }
        return retVal;
    }



    /**
     * TODO: Make this method return a SingleMovie
     * method to query DB for ratings matching username
     * @param email to search for ratings
     * @param movieID to match
     * @return ResultSet (which can be accessed through a for-while loop)
     */
    public SingleMovie getRating(String email, long movieID) {
        SingleMovie currentMovie = null;
        try {
            if (connection == null) connect();
            statement = connection.createStatement();
            //keep making new statements as security method to keep buggy code from accessing
            // old data
            String request = "SELECT MovieID, MovieName, NumericalRating," +
                    "TextReview, PhotoURL, Email, Synopsis FROM sql5107476.RatingInfo WHERE MovieID="
                     + movieID + " AND Email ='" + email + "'";
            ResultSet current = statement.executeQuery(request);
            if (current.next()) {
                currentMovie = new SingleMovie();
                Rating currentRating = new Rating();
                currentMovie.setId((long) current.getDouble("MovieID"));
                currentMovie.setTitle(current.getString("MovieName"));
                currentMovie.setThumbnailURL(current.getString("PhotoURL"));
                currentMovie.setSynopsis(current.getString("Synopsis"));
                currentRating.setUser(current.getString("Email"));
                currentRating.setNumericalRating(current.getInt("NumericalRating"));
                currentRating.setTextReview(current.getString("TextReview"));
                currentMovie.addUserRating(current.getString("Email"), currentRating);
            }
        } catch (SQLException sqle) {
            Log.e("Database SQLException", sqle.getMessage());
            Log.e("Database SQLState", sqle.getSQLState());
            Log.e("Database VendorError", Integer.toString(sqle.getErrorCode()));
        }
        return currentMovie;
    }






    /**
     * Method to add review to database. Screens info to prevent duplicates.
     * @param email email of current user
     * @return boolean true if successfully created
     * @throws SQLException see error message
     */
    public boolean setRating(String email, SingleMovie movie, int numericalRating,
                             String textReview)
            throws SQLException, InputMismatchException {
        ResultSet resultSet = null;
        String request = "";
        String photoURL = movie.getThumbnailURL();
        String movieName = movie.getTitle();
        Log.d("What is the synopsis","It is: " + movie.getSynopsis());
        String synopsis = movie.getSynopsis();
        double movieID = (double) movie.getId();
        try {
            if (numericalRating < 0 || numericalRating > 100) {
                throw new InputMismatchException("Rating must be from 1-100");
            }
            statement = connection.createStatement();
            SingleMovie previous = getRating(email, (long) movieID);
            if (previous != null) {
                Log.d("SQL STRING", request);
                request = "UPDATE sql5107476.RatingInfo SET NumericalRating=" + numericalRating
                        + ", TextReview='" + textReview.replaceAll("'","''") + "' WHERE MovieID="
                        + movieID + " AND Email ='" + email + "'";
                Log.d("SQL STRING", request);
            } else {
                request = "INSERT INTO sql5107476.RatingInfo (MovieID, MovieName, Synopsis, " +
                        "PhotoURL, Email, NumericalRating, TextReview) VALUES ("
                        + movieID + ",'" + movieName + "','" + synopsis.replaceAll("'","''")
                        + "','" + photoURL + "','" + email + "'," + numericalRating + ",'"
                        + textReview.replaceAll("'","''") + "')";
                Log.d("SQL STRING", request);
            }

            statement.executeUpdate(request);
        }
        catch (SQLException e) {
            Log.d("DB Write error", e.getMessage());
            throw e;
        }
        return true;
    }
}