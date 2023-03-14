package main.service.exception;

public class KVTaskClientPutException extends RuntimeException {
    public KVTaskClientPutException(String message) {
        super(message);
    }
}
