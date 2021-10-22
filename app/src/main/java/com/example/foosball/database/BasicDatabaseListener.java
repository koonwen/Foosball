package com.example.foosball.database;

/**
 * Classes implementing this interface can be used to receive events about a database operation.
 */
public interface BasicDatabaseListener extends DatabaseListener {
    /**
     * This method will be called in the event that the database operation is successful.
     */
    void onSuccess();
}
