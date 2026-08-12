package ru.ruslan.exception;

public class StopExecutionException extends RuntimeException {
    public StopExecutionException(String message) {
        super(message);
    }
}