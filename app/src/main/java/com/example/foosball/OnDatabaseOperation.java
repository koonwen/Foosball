package com.example.foosball;

public abstract class OnDatabaseOperation {
    void onConnectionError() {};
    void onInputError(String errorMessage) {};
    void onSuccess() {};
}
