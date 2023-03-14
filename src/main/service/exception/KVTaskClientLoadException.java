package main.service.exception;

public class KVTaskClientLoadException extends RuntimeException {
    public KVTaskClientLoadException(String message) {
        super(message);
    }
}