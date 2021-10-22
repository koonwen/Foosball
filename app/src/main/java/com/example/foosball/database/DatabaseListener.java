package com.example.foosball.database;

/**
 * Classes implementing this interface can be used to receive events about a database connection
 * error. This interface should be extended by another interface that defines the methods to be
 * called upon a successful connection.
 */
interface DatabaseListener {
    /**
     * This method will be called in the event that there is a database connection error.
     */
    void onConnectionError();
}
