package com.example.foosball.database;

/**
 * Classes implementing this interface can be used to receive events about a database connection
 * error. This interface can be extended by other interfaces that define methods to be called upon
 * successful connection.
 */
public interface DatabaseListener {
    /**
     * This method will be called in the event that there is a database connection error.
     */
    void onConnectionError();
}
